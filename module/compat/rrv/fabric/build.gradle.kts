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
    compatModuleDependency(this, "rrv:rrv-common")
    compileOnlyApi(libs.platform.fabric.compat.rrv)
}

val fabricImplProject: Project = platformImplProject("fabric")

addExtraMixinConfig(fabricImplProject, mixinConfigName(compatProject("rrv:rrv-common")))
addExtraMixinConfig(fabricImplProject, mixinConfigName(project))
