import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("ohmega-platform-impl")
    alias(libs.plugins.common.utility.modPublish)
}

val configurationJavaVersion: String = libs.versions.configuration.java.get()
val minecraftVersion: String = libs.versions.minecraft.asProvider().get()
val minecraftVersionRangeMin: String = libs.versions.minecraft.range.min.get()
val modVersion: String = libs.versions.mod.get()

val extraMixinConfigs: MutableList<String> = mutableListOf()

extra.set("ohmegaPlatformExtraMixinConfigs", extraMixinConfigs)

// Fixes some strange behaviour
// Remove when project isolation viable
evaluationDependsOn(apiProject().path)

repositories {
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
}

dependencies {
    moduleDependency(this, commonImplProject())

    if (commonUtilitySparkEnabledRuntime.toBoolean()) {
        runtimeOnly("maven.modrinth:spark:${libs.versions.common.utility.spark.get()}-${project.parent?.name}")
    }
}

tasks.processResources {
    var extraMixinConfigsProcessed = ""

    if (extraMixinConfigs.isNotEmpty()) {
        for (config: String in extraMixinConfigs) {
            if (project.name == "fabric") {
                extraMixinConfigsProcessed += ",\"$config\"\n"
            } else if (project.name == "neoforge") {
                extraMixinConfigsProcessed += "[[mixins]]\nconfig=\"$config\"\n"
            }
        }
    }

    var modDescription: String = modDescriptionSummary

    if (project.name == "forge") {
        modDescription += """
                $modDescriptionSectionCrowdin: $modUrlCrowdin
                $modDescriptionSectionDiscord: $modUrlDiscord
                $modDescriptionSectionSource: $modUrlGithub
                $modDescriptionSectionWiki: $modUrlWiki"""
    }

    if (modChanges.isNotEmpty()) {
        modDescription += "\n\nChanges this version:\n$modChanges"
    }

    if (project.name == "fabric") {
        modDescription = modDescription.replace("\n", "\\n").replace("\"", "\\\"")
    }

    val replacements: Map<String, String> = mapOf(
        "configurationJavaVersion"                               to configurationJavaVersion,
        "extraMixinConfigs"                                      to extraMixinConfigsProcessed,
        "minecraftVersionRangeMax"                               to minecraftVersion,
        "minecraftVersionRangeMin"                               to minecraftVersionRangeMin,
        "modAuthors"                                             to modAuthors,
        "modChanges"                                             to modChanges,
        "modLicense"                                             to modLicense,
        "modDescription"                                         to modDescription,
        "modName"                                                to modName,
        "modUrlCrowdin"                                          to modUrlCrowdin,
        "modUrlCurseforge"                                       to modUrlCurseforge,
        "modUrlDiscord"                                          to modUrlDiscord,
        "modUrlGithub"                                           to modUrlGithub,
        "modId"                                                  to modId,
        "modUrlModrinth"                                         to modUrlModrinth,
        "modUrlWiki"                                             to modUrlWiki,
        "modVersion"                                             to modVersion,
        "platformFabricDependencyForgeConfigApiPortVersionRange" to libs.versions.platform.fabric.dependency.forgeConfigApiPort.range.get(),
        "platformFabricVersionRange"                             to libs.versions.platform.fabric.range.get(),
        "platformForgeVersion"                                   to libs.versions.platform.forge.asProvider().get(),
        "platformForgeVersionRange"                              to libs.versions.platform.forge.range.get(),
        "platformNeoforgeVersion"                                to libs.versions.platform.neoforge.asProvider().get(),
        "platformNeoforgeVersionRange"                           to libs.versions.platform.neoforge.range.get()
    )

    inputs.properties(replacements)

    filesMatching(listOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand(replacements)
    }

    from(configurations.getByName("commonResources"))
    // Fixes Forge and NeoForge not pulling in generated resources
    from(commonImplProject().sourceSets.main.get().resources.sourceDirectories.files.toList()[1])

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Jar> ("jar") {
    from(configurations.getByName("commonResources"))
    exclude("**/${metaFileName(commonImplProject().apiProject())}.accesswidener")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile>().configureEach {
    val configuration: Configuration = configurations.getByName("commonJava")

    dependsOn(configuration)
    source(configuration)
}

tasks.named<Jar> ("sourcesJar") {
    val commonImplProject: Project = commonImplProject()

    from(
            commonImplProject.sourceSets.main.get().allSource,
            commonImplProject.apiProject().sourceSets.main.get().allSource,
            apiProject().sourceSets.main.get().allSource)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

if (rootProject.file("secrets.properties").exists()) {
    afterEvaluate {
        publishMods {
            val name: String = if (project.name == "neoforge") {
                "NeoForge"
            } else {
                project.name.capitalized()
            }

            changelog = modChanges.replace("| ", "+ ")
            displayName = "$modName $modVersion $name $minecraftVersion"

            file.set(tasks.named<Jar>("jar").flatMap {
                it.archiveFile
            })

            type = if (modVersion.contains("alpha")) {
                ALPHA
            } else if (modVersion.contains("beta")) {
                BETA
            } else {
                STABLE
            }

            additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap {
                it.archiveFile
            })

            modLoaders.add(project.name)

            // todo: see if i can add sgui as bundled dep on fabric when polymer compat is on, and other optional deps
            curseforge {
                accessToken.set(findProperty("secretsCurseForgeToken").toString())
                client = true
                projectId = "889368"
                server = true

                javaVersions.add(JavaVersion.toVersion(configurationJavaVersion))

                minecraftVersionRange {
                    start = minecraftVersionRangeMin
                    end = minecraftVersion
                }

                if (project.name == "fabric") {
                    requires("fabric-api")
                    requires("forge-config-api-port")
                }
            }

            // todo: see if i can add sgui as bundled dep on fabric when polymer compat is on, and other optional deps
            modrinth {
                accessToken.set(findProperty("secretsModrinthToken").toString())
                projectId = "scvT0o5w"
                version = modVersion

                minecraftVersionRange {
                    start = minecraftVersionRangeMin
                    end = minecraftVersion
                }

                if (project.name == "fabric") {
                    requires("fabric-api")
                    requires("forge-config-api-port")
                }
            }
        }
    }
}
