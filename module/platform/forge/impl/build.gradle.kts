plugins {
    id("ohmega-loader-impl")
    alias(libs.plugins.platform.forge.fg)
}

val minecraftVersion: String = libs.versions.minecraft.asProvider().get()

val commonImplProject: Project = commonImplProject()
val nativeMixinConfigName: String = mixinConfigName(project)
val apiProject: Project = apiProject()
val nativeApiMixinConfigName: String = mixinConfigName(apiProject)
val commonMixinConfigName: String = mixinConfigName(commonImplProject)

minecraft {
    mappings("official", minecraftVersion)

    setAccessTransformers(apiProject.files("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        configureEach {
            mods {
                register (modId) {
                    source(commonImplProject.apiProject().sourceSets.main.get())
                    source(commonImplProject.sourceSets.main.get())
                    source(apiProject.sourceSets.main.get())
                    source(sourceSets.main.get())
                }
            }

            args("--mixin.config", nativeMixinConfigName)
            args("--mixin.config", nativeApiMixinConfigName)
            args("--mixin.config", commonMixinConfigName)
            environment(mapOf("MOD_CLASSES" to "{source_roots}"))
            systemProperty("eventbus.api.strictRuntimeChecks", "true")
            systemProperty("forge.enabledGameTestNamespaces", modId)
            systemProperty("mixin.debug.export", "true")
            workingDir.convention(layout.projectDirectory.dir("run"))
        }

        register("client")

        register ("server") {
            args("--nogui")
        }
    }
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:$minecraftVersion-${libs.versions.platform.forge.asProvider().get()}"))
    annotationProcessor(libs.platform.forge.eventbusValidator)
}

tasks.named<Jar> ("jar") {
    manifest {
        attributes(mapOf("MixinConfigs" to "$nativeMixinConfigName, $nativeApiMixinConfigName, $commonMixinConfigName"))
    }
}