plugins {
    id("dev.kikugie.stonecutter")
    alias(libs.plugins.publishing)
}

stonecutter active "1.21.11-fabric" /* [SC] DO NOT EDIT */

stonecutter tasks {
    order("publishMods", versionComparator)
}

stonecutter parameters {
    val loader = node.project.property("loom.platform")
    constants["fabric"] = loader == "fabric"
    constants["neoforge"] = loader == "neoforge"
}

tasks.named("publishMods") {
    group = "build"
}