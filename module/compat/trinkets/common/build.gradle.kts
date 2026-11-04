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
    compileOnly(libs.platform.common.compat.trinkets)
}
