plugins {
    `java-library`
}

val configurationJavaVersion: String = libs.versions.configuration.java.get()

group = "com.swacky"
version = "${libs.versions.mod.get()}-mc${libs.versions.minecraft.asProvider().get()}"

java {
    val javaLanguageVersion: JavaLanguageVersion = JavaLanguageVersion.of(configurationJavaVersion)
    val javaVersion: JavaVersion = JavaVersion.toVersion(configurationJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    toolchain.languageVersion = javaLanguageVersion

    // This artifact isn't used, it is just to error out if any Javadoc '{@link ...}' references are incorrect
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(configurationJavaVersion.toInt())
}

tasks.jar {
    exclude("**/.cache")
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).tags = listOf(
        "apiNote:a:API Note:",
        "implSpec:a:Implementation Requirements:",
        "implNote:a:Implementation Note:"
    )
}

tasks.named<Jar> ("sourcesJar") {
    exclude("**/.cache")
}

tasks.named ("compileTestJava") {
    enabled = false
}

tasks.named ("processTestResources") {
    enabled = false
}

listOf("apiElements", "runtimeElements", "sourcesElements").forEach { variant ->
    configurations.named (variant) {
        outgoing {
            capability("$group:$modId-${project.name}:$version")
        }
    }
}

configurations {
    register ("commonJava") {
        isCanBeConsumed = true
        isCanBeResolved = true
    }

    register ("commonResources") {
        isCanBeConsumed = true
        isCanBeResolved = true
    }
}

artifacts {
    val sourceSet: SourceSet = sourceSets.main.get()

    add("commonJava", sourceSet.java.sourceDirectories.singleFile)
    add("commonResources", sourceSet.resources.sourceDirectories.singleFile)
}
