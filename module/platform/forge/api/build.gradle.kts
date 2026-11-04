plugins {
    id("ohmega-loader-api")
    alias(libs.plugins.platform.forge.fg)
}

minecraft {
    useDefaultAccessTransformer()
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${libs.versions.minecraft.asProvider().get()}-${libs.versions.platform.forge.asProvider().get()}"))
}