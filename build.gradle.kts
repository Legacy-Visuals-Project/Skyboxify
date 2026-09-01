@file:OptIn(StonecutterExperimentalAPI::class)

import dev.kikugie.stonecutter.StonecutterExperimentalAPI

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.loom.remap)
    alias(libs.plugins.publishing)
    alias(libs.plugins.blossom)
    alias(libs.plugins.ploceus)
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
    val minecraftVersion = property("mod.minecraft_version") as String
    val minecraftVersionRange = property("mod.minecraft_version_range") as String
}

class Dependencies {
    val fabricLoaderVersion = property("deps.fabric_loader_version") as String?
    val ornitheApiVersion = property("deps.ornithe_api_version") as String?
    val featherVersion = property("deps.feather_version") as String?
    val oslVersion = property("deps.osl_version") as String?
    val devAuthVersion = property("deps.devauth_version") as String?
}

val mod = ModData()
val deps = Dependencies()

class LoaderData {
    val name = property("loader.platform") as String?
    val isFabric = "fabric".equals(name, ignoreCase = true)
    val isOrnithe = "ornithe".equals(name, ignoreCase = true)
    val isFabricLike = isFabric || isOrnithe
}

val loader = LoaderData()

val versionString = "${mod.version}+${mod.minecraftVersion}-${loader.name}"
group = mod.group
base {
    archivesName.set("${mod.id}-${versionString}")
}

ploceus {
    setIntermediaryGeneration(2)
}

loom {
    runConfigs.remove(runConfigs["server"]) // Removes server run configs
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }

    runs {
        afterEvaluate {
            configureEach {
                property("mixin.hotSwap", "true")
                property("mixin.debug.export", "true") // Puts mixin outputs in /run/.mixin.out
                property("devauth.enabled", "true")
                property("devauth.account", "main")
            }
        }
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // DevAuth
    maven("https://libraries.minecraft.net/") // DataFixerUpper
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.minecraftVersion}")

    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        mappings(ploceus.featherMappings(deps.featherVersion))
        mappings(rootProject.file("gradle/feather-overrides.tiny"))
    })

    include(implementation("com.mojang:datafixerupper:10.0.21")!!)
    include(implementation("org.joml:joml:1.10.8")!!)

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:${deps.devAuthVersion}")
    if (loader.isFabricLike) {
        modImplementation("net.fabricmc:fabric-loader:${deps.fabricLoaderVersion}")
        if (loader.isOrnithe) {
            ploceus.dependOsl(deps.oslVersion)
        }

        optionalProp("deps.modmenu_version") { prop ->
            modImplementation("com.terraformersmc:modmenu:$prop+mc${mod.minecraftVersion}")
        }
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
    file = tasks.remapJar.flatMap { it.archiveFile }

    val niceVersionRangeTitle = if (mod.minecraftVersionRange.contains(' ')) {
        val parts = mod.minecraftVersionRange.trim().split(' ')
        parts.first() + '-' + parts.last()
    } else {
        mod.minecraftVersionRange
    }

    displayName = "Release ${mod.version} for $niceVersionRangeTitle"
    version = mod.version
    changelog = project.rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."
    type = STABLE

    modLoaders.add(loader.name ?: "fabric")

    dryRun = modrinthId == null && curseforgeId == null
    if (modrinthId != null) {
        modrinth {
            projectId = modrinthId
            accessToken = findProperty("modrinth.token").toString()
            minecraftVersions.addAll(mod.minecraftVersionRange.split(' '))
            if (loader.isFabricLike) {
                requires("fabric-api")
                optional("modmenu")
            }
        }
    }

    if (curseforgeId != null) {
        curseforge {
            projectId = curseforgeId
            projectSlug = mod.id
            accessToken = findProperty("curseforge.token").toString()
            minecraftVersions.addAll(mod.minecraftVersionRange.split(' '))
            client = true
            if (loader.isFabricLike) {
                requires("fabric-api")
                optional("modmenu")
            }
        }
    }

    val discordWebhookUrl = findProperty("discord.webhook")?.toString()?.takeIf { it.isNotBlank() }
    if (discordWebhookUrl != null) {
        discord {
            webhookUrl = discordWebhookUrl
        }
    }
}

java {
    val requiredJava = JavaVersion.VERSION_25
    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
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

            put(
                "minecraft_version_range", if (mod.minecraftVersionRange.contains(' ')) {
                    val parts = mod.minecraftVersionRange.trim().split(' ')
                    ">=" + parts.first() + ' ' + "<=" + parts.last()
                } else {
                    mod.minecraftVersionRange
                }
            )

            if (loader.isFabricLike) {
                put("fabric_loader_version", deps.fabricLoaderVersion)
            }
        }

        props.forEach(inputs::property)
        filesMatching("**/lang/en_us.json") { // Defaults description to English translation
            expand(props)
            filteringCharset = "UTF-8"
        }

        if (loader.isFabricLike) {
            filesMatching("fabric.mod.json") { expand(props) }
        }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar, remapSourcesJar)
        into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
        dependsOn("build")
    }
}

val currentCommitHash: String by lazy {
    Runtime.getRuntime()
        .exec(arrayOf("git", "rev-parse", "--verify", "--short", "HEAD"), null, rootDir)
        .inputStream.bufferedReader().readText().trim()
}

blossom {
    replaceToken("@MODID@", mod.id)
    replaceToken("@VERSION@", mod.version)
    replaceToken("@COMMIT_HASH@", currentCommitHash)
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(tasks.named("build"))
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)