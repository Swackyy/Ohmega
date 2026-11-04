plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "Nucleoid"
        url = uri("https://maven.nucleoid.xyz")
    }
}

dependencies {
    compileOnlyApi(libs.platform.fabric.compat.polymer.core)
    compileOnlyApi(libs.platform.fabric.compat.polymer.resourcePack)
    compileOnlyApi(libs.platform.fabric.compat.polymer.sgui)
}