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
    compatModuleDependency(this, "rei:rei-common")
    compileOnlyApi(libs.platform.neoforge.compat.rei)
}
