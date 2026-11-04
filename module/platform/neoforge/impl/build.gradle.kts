import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("ohmega-loader-impl")
    alias(libs.plugins.platform.neoforge.mdg)
}

val apiProject: Project = apiProject()

neoForge {
    version = libs.versions.platform.neoforge.asProvider().get()
    validateAccessTransformers = true

    mods {
        register (modId) {
            val commonImplProject: Project = commonImplProject()

            sourceSet(commonImplProject.apiProject().sourceSets.main.get())
            sourceSet(commonImplProject.sourceSets.main.get())
            sourceSet(apiProject.sourceSets.main.get())
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        configureEach {
            ideName.set("NeoForge ${name.capitalized()} ($path)")
            systemProperty("mixin.debug.export", "true")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        register ("client") {
            client()
        }

        register ("server") {
            server()

            programArgument("--nogui")
        }
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
        name = "Nucleoid"
        url = uri("https://maven.nucleoid.xyz")
    }
}

dependencies {
    accessTransformers(apiProject)

    if (compatJeiEnabled.toBoolean()) {
        compatModuleDependency(this, "jei")

        if (compatJeiEnabledRuntime.toBoolean()) {
            runtimeOnly("mezz.jei:jei-${libs.versions.minecraft.asProvider().get()}-neoforge:${libs.versions.compat.jei.get()}")
        }
    }

    if (compatReiEnabled.toBoolean()) {
        compatModuleDependency(this, "rei:rei-neoforge")

        if (compatReiEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.neoforge.compat.rei)

            // todo: check to see if this is needed at all
            /*constraints {
                runtimeOnly("dev.architectury:architectury-neoforge:21.0.7")
            }*/
        }
    }

    if (compatRrvEnabled.toBoolean()) {
        compatModuleDependency(this, "rrv:rrv-neoforge")

        if (compatRrvEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.neoforge.compat.rrv)
        }
    }

    if (compatTrinketsEnabled.toBoolean()) {
        compatModuleDependency(this, "trinkets:trinkets-neoforge")

        if (compatTrinketsEnabledRuntime.toBoolean()) {
            runtimeOnly(libs.platform.common.compat.trinkets)
        }
    }
}
