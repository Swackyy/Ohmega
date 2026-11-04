pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net")
        }

        maven {
            name = "Forge"
            url = uri("https://maven.minecraftforge.net")
        }

        maven {
            name = "NeoForge"
            url = uri("https://maven.neoforged.net/releases")
        }

        maven {
            name = "Sponge"
            url = uri("https://repo.spongepowered.org/repository/maven-public")
        }
    }
}

plugins {
    id("dev.panuszewski.typesafe-conventions") version "0.11.1"
}

rootProject.name = "build-logic"
