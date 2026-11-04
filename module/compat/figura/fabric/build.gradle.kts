plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
}

val fabricApiProject: Project = platformApiProject("fabric")

// Remove when project isolation viable
evaluationDependsOn(fabricApiProject.path)

dependencies {
    apiModuleDependency(this, fabricApiProject)
    compatModuleDependency(this, "figura:figura-common")
    compileOnly(libs.platform.fabric.dependency.fabricApi)
    compileOnlyApi(libs.platform.fabric.compat.figura)
    compileOnlyApi(libs.platform.fabric.compat.figura.luaj)
}

/*
Project fabricImplProject = platformImplProject("fabric")

evaluationDependsOn fabricImplProject.path
fabricImplProject.extraMixinConfigs += mixinConfigName project
*/
