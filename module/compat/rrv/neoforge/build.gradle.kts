plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "Cassian"
        url = uri("https://maven.cassian.cc")
    }

    maven {
        name = "NeoForge"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    compatModuleDependency(this, "rrv:rrv-common")
    compileOnly("net.neoforged:neoforge:${libs.versions.platform.neoforge.asProvider().get()}")
    compileOnly("net.neoforged:bus:8.0.5")
    compileOnlyApi(libs.platform.neoforge.compat.rrv)
}

val neoforgeImplProject: Project = platformImplProject("neoforge")

addExtraMixinConfig(neoforgeImplProject, mixinConfigName(compatProject("rrv:rrv-common")))
addExtraMixinConfig(neoforgeImplProject, mixinConfigName(project))
