plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(libs.platform.fabric.compat.figura)
    compileOnly(libs.platform.fabric.compat.figura.luaj)
}
