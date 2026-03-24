@file:OptIn(StonecutterExperimentalAPI::class)

import com.google.devtools.ksp.processing.parseBoolean
import dev.kikugie.stonecutter.StonecutterExperimentalAPI
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.loom.remap) apply false
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
    val obfuscated = parseBoolean(property("mod.obfuscated") as String)
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

val mod = ModData()
val deps = Dependencies()

// Apply specific loom
if (mod.obfuscated) {
    apply(plugin = "net.fabricmc.fabric-loom-remap")
} else {
    apply(plugin = "net.fabricmc.fabric-loom")
}

class LoaderData {
    val name = property("loader.platform") as String?
    val isFabric = "fabric".equals(name, ignoreCase = true)
    val isNeoForge = "neoforge".equals(name, ignoreCase = true)
}

val loader = LoaderData()

val versionString = "${mod.version}+${mod.minecraftVersion}-${loader.name}"
group = mod.group
base {
    archivesName.set("${mod.id}-${versionString}")
}

stonecutter {
    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("level.getDayTime", "level.getOverworldClockTime")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("method_62215", $$"lambda$addSkyPass$0")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("Lnet/minecraft/client/renderer/state/SkyRenderState", "Lnet/minecraft/client/renderer/state/level/SkyRenderState")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("net.minecraft.client.renderer.state.SkyRenderState", "net.minecraft.client.renderer.state.level.SkyRenderState")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("ClientCommandManager", "ClientCommands")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace(".render(guiGraphics", ".extractRenderState(guiGraphics")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("public void render(final GuiGraphics guiGraphics", "public void extractRenderState(final GuiGraphicsExtractor guiGraphics")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("GuiGraphics guiGraphics", "GuiGraphicsExtractor guiGraphics")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("guiGraphics.drawString", "guiGraphics.text")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("guiGraphics.hLine", "guiGraphics.horizontalLine")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("net.minecraft.client.gui.GuiGraphics", "net.minecraft.client.gui.GuiGraphicsExtractor")
    }

    replacements.string {
        direction = eval(current.version, ">=1.21.11")
        replace("ResourceLocation", "Identifier")
    }

    replacements.string {
        direction = eval(current.version, ">=1.21.11")
        replace(".location()", ".identifier()")
    }
}

// loom {
extensions.configure<LoomGradleExtensionAPI> {
    runConfigs.all {
        ideConfigGenerated(stonecutter.current.isActive)
        runDir = "../../run"
    }

    runConfigs.remove(runConfigs["server"]) // Removes server run configs

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

val loom: LoomGradleExtensionAPI by extensions
val fabricApi: FabricApiExtension by extensions
val minecraft by configurations.existing
val include by configurations.existing
val modImplementation: NamedDomainObjectProvider<Configuration> =
    configurations.named(if (mod.obfuscated) "modImplementation" else "implementation")
val modRuntimeOnly: NamedDomainObjectProvider<Configuration> =
    configurations.named(if (mod.obfuscated) "modRuntimeOnly" else "runtimeOnly")

dependencies {
    minecraft("com.mojang:minecraft:${mod.minecraftVersion}")

    if (mod.obfuscated) {
        val mappings by configurations.existing

        @Suppress("UnstableApiUsage")
        mappings(loom.layered {
            officialMojangMappings()

            optionalProp("deps.parchment_version") {
                parchment("org.parchmentmc.data:parchment-${mod.minecraftVersion}:$it@zip")
            }
        })
    }

    compileOnly("org.projectlombok:lombok:${deps.lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${deps.lombokVersion}")
    modRuntimeOnly("me.djtheredstoner:DevAuth-${loader.name}:${deps.devAuthVersion}")

    // LightConfig
    include(modImplementation("org.visuals.legacy:lightconfig:${deps.lightConfigVersion}-${mod.minecraftVersion}_${loader.name}")!!)

    include(implementation("com.moulberry:mixinconstraints:${deps.mixinconstraintsVersion}")!!)!!
    include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-${loader.name}:${deps.mixinsquaredVersion}")!!)!!)
    if (loader.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${deps.fabricLoaderVersion}")

        modImplementation(fabricApi.module("fabric-resource-loader-v0", deps.fabricApiVersion!!))
        modImplementation(fabricApi.module("fabric-command-api-v2", deps.fabricApiVersion))

        optionalProp("deps.modmenu_version") { prop ->
            modImplementation("com.terraformersmc:modmenu:${prop}") {
                exclude(group="net.fabricmc.fabric-api")
            }
        }
    } else if (loader.isNeoForge) {
        // TODO: "neoForge"("net.neoforged:neoforge:${deps.neoForgeVersion}")
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
    file =
        (if (mod.obfuscated) tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") else tasks.jar).flatMap { it.archiveFile }

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
            if (loader.isFabric) {
                requires("fabric-api")
                optional("modmenu")
            }
        }
    }

    if (curseforgeId != null) {
        curseforge {
            projectId = curseforgeId
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
    val requiredJava = when {
        stonecutter.eval(stonecutter.current.version, ">=26.1") -> JavaVersion.VERSION_25
        stonecutter.eval(stonecutter.current.version, ">=1.20.5") -> JavaVersion.VERSION_21
        stonecutter.eval(stonecutter.current.version, ">=1.18") -> JavaVersion.VERSION_17
        stonecutter.eval(stonecutter.current.version, ">=1.17") -> JavaVersion.VERSION_16
        else -> JavaVersion.VERSION_1_8
    }

    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
    if (!mod.obfuscated) {
        withSourcesJar()
    }
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
                put(
                    "fabric_resource_loader_dep", if (stonecutter.eval(stonecutter.current.version, ">=1.21.10"))
                        "fabric-resource-loader-v1"
                    else
                        "fabric-resource-loader-v0"
                )
            }

            if (loader.isNeoForge) {
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

        if (loader.isNeoForge) {
            filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
            exclude("fabric.mod.json")
        }
    }

    register<Copy>("buildAndCollect") {
        group = "build"

        if (mod.obfuscated) {
            val remapJar by existing(net.fabricmc.loom.task.RemapJarTask::class)
            val remapSourcesJar by existing(net.fabricmc.loom.task.RemapSourcesJarTask::class)
            from(remapJar, remapSourcesJar)
        } else {
            val sourcesJar by existing
            from(jar, sourcesJar)
        }

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
