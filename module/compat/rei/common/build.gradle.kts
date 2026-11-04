plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "Architectury"
        url = uri("https://maven.architectury.dev")
    }
}

dependencies {
    compatModuleDependency(this, "util:util-exclusion-zone")
    compileOnly(libs.platform.common.compat.rei)
}