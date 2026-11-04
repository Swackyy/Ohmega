import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("ohmega-loader-impl")
    alias(libs.plugins.platform.fabric.loom)
}

val commonImplProject: Project = commonImplProject()
val apiProject: Project = apiProject()

loom {
    accessWidenerPath.set(accessWidenerFile(apiProject))

    runs {
        configureEach {
            displayName = "Fabric ${name.capitalized()}"
            generateRunConfig = true

            systemProperties.put("mixin.debug.export", "true")
        }

        named("client") {
            programArguments.addAll("--username", "Dev")
        }

        register("client2") {
            client()
            programArguments.addAll("--username", "AlsoDev")
        }

        named("server")
    }
}

repositories {
    maven {
        name = "Architectury"
        url = uri("https://maven.architectury.dev")
    }

    maven {
        name = "BlameJared"
        url = uri("https://maven.blamejared.com")
    }

    maven {
        name = "Cassian"
        url = uri("https://maven.cassian.cc")
    }

    maven {
        name = "Fuzss"
        url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven")
    }

    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }

    maven {
        name = "Nucleoid"
        url = uri("https://maven.nucleoid.xyz")
    }

    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/releases")
    }
}

dependencies {
    minecraft(libs.platform.fabric.minecraft)
    implementation(libs.platform.fabric)
    implementation(libs.platform.fabric.dependency.fabricApi)
    implementation(libs.platform.fabric.dependency.forgeConfigApiPort)

    if (platformFabricUtilityModMenuEnabledRuntime.toBoolean()) {
        runtimeOnly(libs.platform.fabric.utility.modMenu)
    }

    if (compatFiguraEnabled.toBoolean()) {
        compatModuleDependency(this, "figura:figura-fabric")

        if (compatFiguraEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.fabric.compat.figura)
        }
    }

    if (compatJeiEnabled.toBoolean()) {
        compatModuleDependency(this, "jei")

        if (compatJeiEnabledRuntime.toBoolean()) {
            runtimeOnly("mezz.jei:jei-${libs.versions.minecraft.asProvider().get()}-fabric:${libs.versions.compat.jei.get()}")
        }
    }

    @Suppress("AvoidDuplicateDependencies")
    if (compatPolymerEnabled.toBoolean()) {
        compatModuleDependency(this, "polymer")
        include(libs.platform.fabric.compat.polymer.sgui)

        if (compatPolymerEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.fabric.compat.polymer)
            runtimeOnly(libs.platform.fabric.compat.polymer.sgui)
        }
    }

    if (compatReiEnabled.toBoolean()) {
        compatModuleDependency(this, "rei:rei-fabric")

        if (compatReiEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.fabric.compat.rei)
        }
    }

    if (compatRrvEnabled.toBoolean()) {
        compatModuleDependency(this, "rrv:rrv-fabric")

        if (compatRrvEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.fabric.compat.rrv)
        }
    }

    if (compatTrinketsEnabled.toBoolean()) {
        compatModuleDependency(this, "trinkets:trinkets-fabric")

        if (compatTrinketsEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.common.compat.trinkets)
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = true
        modId = project.modId
        outputDirectory = commonImplProject().sourceSets.main.get().resources.sourceDirectories.files.toList()[1]
    }
}