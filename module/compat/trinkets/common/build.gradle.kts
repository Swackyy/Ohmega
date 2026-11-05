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
    compatModuleDependency(this, "util:util-mixin-config")
    compileOnly(libs.platform.common.compat.trinkets)
}
