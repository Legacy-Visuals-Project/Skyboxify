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
    val id = property("mod.id").toString()
    val name = property("mod.name")
    val version = property("mod.version")
    val group = property("mod.group").toString()
    val description = property("mod.description")
    val source = property("mod.source")
    val issues = property("mod.issues")
    val license = property("mod.license").toString()
    val modrinth = property("mod.modrinth")
    val curseforge = property("mod.curseforge")
    val discord = property("mod.discord")

    val stable = parseBoolean(property("mod.stable").toString())

    val mcVersion = property("mod.mc_version")
    val mcVersionRange = property("mod.mc_version_range").toString()
}

class Dependencies {
    val neoForgeVersion = property("deps.neoforge_version")
    val fabricLoaderVersion = property("deps.fabric_loader_version")
    val fabricApiVersion = property("deps.fabric_api_version").toString()
    val mixinconstraintsVersion = property("deps.mixinconstraints_version")
    val mixinsquaredVersion = property("deps.mixinsquared_version")
    val lightConfigVersion = property("deps.lightconfig")
}

class LoaderData {
    val loader = loom.platform.get().name.lowercase()
    val isFabric = loader == "fabric"
    val isNeoforge = loader == "neoforge"
}

val mod = ModData()
val deps = Dependencies()
val loader = LoaderData()

version = "${mod.version}+${mod.mcVersion}-${loader.loader}"
group = mod.group
base { archivesName.set(mod.id) }

stonecutter {
    constants["fabric"] = loader.isFabric
    constants["neoforge"] = loader.isNeoforge
    replacements.string {
        direction = eval(current.version, ">=1.21.11")
        replace("ResourceLocation", "Identifier")
    }
}

blossom {
    replaceToken("@MODID@", mod.id)
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
    maven("https://maven.parchmentmc.org") // Parchment
    maven("https://maven.terraformersmc.com") // Mod Menu
    maven("https://maven.nucleoid.xyz/") // Placeholder API - required by Mod Menu
    maven("https://maven.neoforged.net/releases") // NeoForge
    maven("https://maven.bawnorton.com/releases") // MixinSquared
    maven("https://api.modrinth.com/maven") // Modrinth
    maven("https://jitpack.io") // LightConfig
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.mcVersion}")

    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        // Mojmap mappings
        officialMojangMappings()
        // Parchment mappings (it adds parameter mappings & javadoc)
        optionalProp("deps.parchment_version") {
            var snapshot = !mod.mcVersion.toString().contains(".")
            parchment("org.parchmentmc.data:parchment-${if (snapshot) "1.21.10" else mod.mcVersion}:$it@zip")
        }
    })

    // LightConfig
    include(modImplementation("com.github.Legacy-Visuals-Project:LightConfig:${deps.lightConfigVersion}")!!)

    include(implementation("com.moulberry:mixinconstraints:${deps.mixinconstraintsVersion}")!!)!!
    include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-${loader.loader}:${deps.mixinsquaredVersion}")!!)!!)
    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${deps.fabricLoaderVersion}")

        modImplementation(fabricApi.module("fabric-resource-loader-v0", deps.fabricApiVersion))
        modImplementation(fabricApi.module("fabric-command-api-v2", deps.fabricApiVersion))

        optionalProp("deps.modmenu_version") { prop ->
            modImplementation("com.terraformersmc:modmenu:${prop}")
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
    if (!mod.stable) {
        return@publishMods
    }

    file = project.tasks.remapJar.get().archiveFile
    val niceVersionRangeTitle = if (mod.stable && mod.mcVersionRange.contains(' ')) {
        val parts = mod.mcVersionRange.trim().split(' ')
        parts.first() + '-' + parts.last()
    } else {
        mod.mcVersionRange
    }

    displayName = "Release ${mod.version} for $niceVersionRangeTitle"
    this.version = mod.version.toString()
    changelog = project.rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
    type = STABLE

    modLoaders.add(loader.loader)
    dryRun = modrinthId == null && curseforgeId == null
    if (modrinthId != null) {
        modrinth {
            projectId = property("publish.modrinth").toString()
            accessToken = findProperty("modrinth.token").toString()
            minecraftVersions.addAll(mod.mcVersionRange.split(' '))
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
            minecraftVersions.addAll(mod.mcVersionRange.split(' '))
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

tasks.processResources {
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
        }

        if (loader.isNeoforge) {
            put("forge_version", deps.neoForgeVersion)
        }

        val mcVersionRange = if (mod.stable && mod.mcVersionRange.contains(' ')) {
            val parts = mod.mcVersionRange.trim().split(' ')
            ">=" + parts.first() + ' ' + "<=" + parts.last()
        } else {
            mod.mcVersionRange
        }

        put("minecraft_version_range", mcVersionRange)
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

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(tasks.named("build"))
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)
