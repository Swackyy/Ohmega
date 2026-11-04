import java.util.Properties

plugins {
    idea
    java
    alias(libs.plugins.common.utility.modPublish) apply false
    alias(libs.plugins.platform.common.spongeGradle) apply false
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val secretsFile: File = file("secrets.properties")

if (secretsFile.exists()) {
    val secrets = Properties()

    secrets.load(secretsFile.inputStream())
    secrets.forEach { (key, value) ->
        project.extra.set(key.toString(), value)
    }
}