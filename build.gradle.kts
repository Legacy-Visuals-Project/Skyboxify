import com.google.devtools.ksp.processing.parseBoolean

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.loom)
    alias(libs.plugins.publishing)
    alias(libs.plugins.blossom)
    alias(libs.plugins.ksp)
    alias(libs.plugins.fletchingtable.fabric)
    alias(libs.plugins.fletchingtable.neoforge)
}

class ModData {
    val id = property("mod.id") as String
    val name = property("mod.name") as String
    val version = property("mod.version") as String
    val group = property("mod.group") as String
    val description = property("mod.description") as String
    val source = property("mod.source") as String
    val issues = property("mod.issues") as String
    val license = property("mod.license") as String
    val modrinth = property("mod.modrinth") as String
    val curseforge = property("mod.curseforge") as String
    val discord = property("mod.discord") as String
    val obfuscated = parseBoolean(property("mod.obfuscated")  as String)
    val minecraftVersion = property("mod.minecraft_version") as String
    val minecraftVersionRange = property("mod.minecraft_version_range") as String
}

class Dependencies {
    val neoForgeVersion = property("deps.neoforge_version") as String?
    val fabricLoaderVersion = property("deps.fabric_loader_version") as String?
    val fabricApiVersion = property("deps.fabric_api_version") as String?
    val devAuthVersion = property("deps.devauth_version") as String?
    val mixinconstraintsVersion = property("deps.mixinconstraints_version") as String?
    val mixinsquaredVersion = property("deps.mixinsquared_version") as String?
    val lombokVersion = property("deps.lombok_version") as String?
    val lightConfigVersion = property("deps.lightconfig_version") as String?
}

class LoaderData {
    val name = loom.platform.get().name.lowercase()
    val isFabric = this@LoaderData.name == "fabric"
    val isNeoforge = this@LoaderData.name == "neoforge"
}

val mod = ModData()
val deps = Dependencies()
val loader = LoaderData()

version = "${mod.version}+${mod.minecraftVersion}-${loader.name}"
group = mod.group
base { archivesName.set(mod.id) }

stonecutter {
    replacements.string {
        direction = eval(current.version, ">=1.21.11")
        replace("ResourceLocation", "Identifier")
    }

    replacements.string {
        direction = eval(current.version, ">=1.21.11")
        replace(".location().toString()", ".identifier().toString()")
    }
}

blossom {
    replaceToken("@MODID@", mod.id)
    replaceToken("@VERSION@", mod.version)
}

loom {
    silentMojangMappingsLicense()
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }

    runConfigs.remove(runConfigs["server"]) // Removes server run configs
}

loom.runs {
    afterEvaluate {
        val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
            componentFilter {
                it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
            }
        }.files.first()
        configureEach {
            vmArg("-javaagent:$mixinJarFile") // Mixin Hotswap doesn't work on NeoForge, but doesn't hurt to keep
            property("mixin.hotSwap", "true")
            property("mixin.debug.export", "true") // Puts mixin outputs in /run/.mixin.out
            property("devauth.enabled", "true")
            property("devauth.account", "main")
        }
    }
}

fletchingTable {
    mixins.create("main") {
        mixin("default", "${mod.id}.mixins.json")
    }

    lang.create("main") {
        patterns.add("assets/${mod.id}/lang/**")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.parchmentmc.org") // Parchment
    maven("https://maven.nucleoid.xyz/") // Placeholder API - required by Mod Menu
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // DevAuth
    maven("https://maven.neoforged.net/releases") // NeoForge
    maven("https://maven.bawnorton.com/releases") // MixinSquared
    maven("https://maven.terraformersmc.com") // Mod Menu
    maven("https://api.modrinth.com/maven") // Modrinth
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.minecraftVersion}")

    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        // Mojmap mappings
        officialMojangMappings()
        // Parchment mappings (it adds parameter mappings & javadoc)
        optionalProp("deps.parchment_version") {
            parchment("org.parchmentmc.data:parchment-${mod.minecraftVersion}:$it@zip")
        }
    })

    compileOnly("org.projectlombok:lombok:${deps.lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${deps.lombokVersion}")
    modRuntimeOnly("me.djtheredstoner:DevAuth-${loader.name}:${deps.devAuthVersion}")

    // LightConfig
    include(modImplementation("org.visuals.legacy:lightconfig:${deps.lightConfigVersion}-${mod.minecraftVersion}_${loader.name}")!!)

    include(implementation("com.moulberry:mixinconstraints:${deps.mixinconstraintsVersion}")!!)!!
    include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-${loader.name}:${deps.mixinsquaredVersion}")!!)!!)
    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${deps.fabricLoaderVersion}")

        modImplementation(fabricApi.module("fabric-resource-loader-v0", deps.fabricApiVersion))
        modImplementation(fabricApi.module("fabric-command-api-v2", deps.fabricApiVersion))

        optionalProp("deps.modmenu_version") { prop ->
            modImplementation("com.terraformersmc:modmenu:${prop}") {
                exclude(group, "net.fabricmc.fabric-api")
            }
        }
    } else if (loader.isNeoforge) {
        "neoForge"("net.neoforged:neoforge:${deps.neoForgeVersion}")
    }
}

val modrinthId = findProperty("publish.modrinth")?.toString()?.takeIf { it.isNotBlank() }
val curseforgeId = findProperty("publish.curseforge")?.toString()?.takeIf { it.isNotBlank() }

// accessTokens should be placed in the user Gradle gradle.properties file
// for example, on Windows this would be "C:\Users\{user}\.gradle\gradle.properties"
// then add:
// modrinth.token=
// curseforge.token=
publishMods {
    file = project.tasks.remapJar.get().archiveFile
    val niceVersionRangeTitle = if (mod.minecraftVersionRange.contains(' ')) {
        val parts = mod.minecraftVersionRange.trim().split(' ')
        parts.first() + '-' + parts.last()
    } else {
        mod.minecraftVersionRange
    }

    displayName = "Release ${mod.version} for $niceVersionRangeTitle"
    this.version = mod.version.toString()
    changelog = project.rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
    type = STABLE

    modLoaders.add(loader.name)
    dryRun = modrinthId == null && curseforgeId == null
    if (modrinthId != null) {
        modrinth {
            projectId = property("publish.modrinth").toString()
            accessToken = findProperty("modrinth.token").toString()
            minecraftVersions.addAll(mod.minecraftVersionRange.split(' '))
            if (loader.isFabric) {
                requires("fabric-api")
                optional("modmenu")
            }
        }
    }

    if (curseforgeId != null) {
        curseforge {
            projectId = property("publish.curseforge").toString()
            accessToken = findProperty("curseforge.token").toString()
            minecraftVersions.addAll(mod.minecraftVersionRange.split(' '))
            if (loader.isFabric) {
                requires("fabric-api")
                optional("modmenu")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    processResources {
        val props = buildMap {
            put("id", mod.id)
            put("name", mod.name)
            put("version", mod.version)
            put("description", mod.description)
            put("source", mod.source)
            put("issues", mod.issues)
            put("license", mod.license)
            put("modrinth", mod.modrinth)
            put("curseforge", mod.curseforge)
            put("discord", mod.discord)
            if (loader.isFabric) {
                put("fabric_loader_version", deps.fabricLoaderVersion)
                put("fabric_resource_loader_dep", if (stonecutter.eval(stonecutter.current.version, ">=1.21.10"))
                    "fabric-resource-loader-v1"
                else
                    "fabric-resource-loader-v0"
                )
            }

            if (loader.isNeoforge) {
                put("neoforge_version", deps.neoForgeVersion)
            }

            val minecraftVersionRange = if (mod.minecraftVersionRange.contains(' ')) {
                val parts = mod.minecraftVersionRange.trim().split(' ')
                ">=" + parts.first() + ' ' + "<=" + parts.last()
            } else {
                mod.minecraftVersionRange
            }

            put("minecraft_version_range", minecraftVersionRange)
        }

        props.forEach(inputs::property)
        filesMatching("**/lang/en_us.json") { // Defaults description to English translation
            expand(props)
            filteringCharset = "UTF-8"
        }

        if (loader.isFabric) {
            filesMatching("fabric.mod.json") { expand(props) }
            exclude(listOf("META-INF/neoforge.mods.toml"))
        }

        if (loader.isNeoforge) {
            filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
            exclude("fabric.mod.json")
        }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(tasks.named("build"))
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)
