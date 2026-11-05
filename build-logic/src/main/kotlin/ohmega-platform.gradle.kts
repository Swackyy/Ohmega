import groovy.util.Node
import groovy.util.NodeList
import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("ohmega-module")
    `maven-publish`
    signing
}

base {
    archivesName.set("${modId}-${project.name}")
}

tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}

tasks.named<Jar> ("javadocJar") {
    archiveClassifier = "javadoc-unused"
}

tasks.register<Jar> ("pseudoJavadocJar") {
    description = "Produces an empty Javadoc Jar to reduce publication size"
    archiveClassifier = "javadoc"
}

tasks.processResources {
    val replacements: Map<String, String> = mapOf(
            "minecraftPackFormatVersionRangeMax" to libs.versions.minecraft.packFormat.range.max.get(),
            "minecraftPackFormatVersionRangeMin" to libs.versions.minecraft.packFormat.range.min.get()
    )

    inputs.properties(replacements)

    filesMatching (listOf("pack.mcmeta", "resourcepacks/*/pack.mcmeta")) {
        expand(replacements)
    }
}

if (rootDir.resolve("secrets.properties").exists()) {
    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication> ("maven") {
                    groupId = "com.swacky"
                    artifactId = base.archivesName.get()

                    from(components.getByName("java"))
                    artifact(tasks.named("pseudoJavadocJar"))

                    artifacts.removeAll { artifact ->
                        artifact.classifier == "javadoc-unused"
                    }

                    pom {
                        name = "$modName ${project.name.capitalized()}"
                        description = modDescriptionSummary
                        url = modUrlGithub

                        developers {
                            developer {
                                id = "swackyy"
                                name = modAuthors
                            }
                        }

                        licenses {
                            //noinspection GroovyAssignabilityCheck
                            license {
                                name = modLicense
                            }
                        }

                        // todo: check removals, testing with all compats enabled
                        withXml {
                            (asNode().get("dependencies") as NodeList?)?.getAt("dependency")?.filterIsInstance<Node>()?.forEach { dep ->
                                val groupId: String = (dep.get("groupId") as NodeList).text()
                                val artifactId: String = (dep.get("artifactId") as NodeList).text()

                                if (
                                    artifactId.contains("asm")                    ||
                                    groupId.contains("fabricmc")                  ||
                                    artifactId.contains("jei")                    ||
                                    groupId.contains("minecraftforge")            ||
                                    artifactId.contains("modmenu")                ||
                                    artifactId.contains("polymer")                ||
                                    artifactId.contains("reliable-recipe-viewer") ||
                                    artifactId.contains("RoughlyEnoughItems")     ||
                                    artifactId.contains("sgui")                   ||
                                    artifactId.contains("spark")                  ||
                                    artifactId.contains("trinkets")) {
                                    dep.parent().remove(dep)
                                }
                            }
                        }
                    }

                    suppressPomMetadataWarningsFor("apiElements")
                    suppressPomMetadataWarningsFor("javadocElements")
                    suppressPomMetadataWarningsFor("runtimeElements")
                    suppressPomMetadataWarningsFor("sourcesElements")
                }
            }

            repositories {
                maven {
                    name = "Novatores"
                    url = uri("https://maven.novatores.net")

                    credentials {
                        username = findProperty("secretsNovatoresUsername").toString()
                        password = findProperty("secretsNovatoresPassword").toString()
                    }
                }
            }
        }

        plugins.withType<MavenPublishPlugin>().configureEach {
            signing {
                val id: String? = findProperty("secretsSigningId")?.toString()
                val key: String? = findProperty("secretsSigningKey")?.toString()
                val password: String? = findProperty("secretsSigningPassword")?.toString()

                if (id != null && key != null && password != null) {
                    useInMemoryPgpKeys(id, key, password)
                    sign(publishing.publications.getByName("maven"))
                } else {
                    System.err.println("Could not sign project ${project.name}")
                }
            }
        }
    }
}
