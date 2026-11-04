plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "BlameJared"
        url = uri("https://maven.blamejared.com")
    }
}

dependencies {
    compatModuleDependency(this, "util:util-exclusion-zone")
    compileOnlyApi("mezz.jei:jei-${libs.versions.minecraft.asProvider().get()}-fabric:${libs.versions.compat.jei.get()}")
}