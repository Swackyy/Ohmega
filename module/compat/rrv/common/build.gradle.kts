plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "Cassian"
        url = uri("https://maven.cassian.cc")
    }
}

dependencies {
    compatModuleDependency(this, "util:util-exclusion-zone")
    // Using the Fabric specification in common as RRV does not ship a common artifact, it should be fine
    compileOnly(libs.platform.fabric.compat.rrv)
}