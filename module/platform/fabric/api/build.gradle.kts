plugins {
    id("ohmega-loader-api")
    alias(libs.plugins.platform.fabric.loom)
}

val minecraftVersion: String = libs.versions.minecraft.asProvider().get()

repositories {
    maven {
        name = "Fuzss"
        url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven")
    }
}

loom {
    accessWidenerPath.set(accessWidenerFile(project))
}

dependencies {
    minecraft(libs.platform.fabric.minecraft)
    compileOnly(libs.platform.fabric)
    // Uses 'implementation' so ClassTweaker still applies
    implementation(libs.platform.fabric.dependency.fabricApi)
    compileOnly(libs.platform.fabric.dependency.forgeConfigApiPort)
}