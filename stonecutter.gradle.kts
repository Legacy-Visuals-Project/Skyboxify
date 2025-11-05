plugins {
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.publishing)
}

stonecutter active "1.21.11-fabric" /* [SC] DO NOT EDIT */

stonecutter tasks {
    order("publishMods", versionComparator)
}

tasks.named("publishMods") {
    group = "build"
}