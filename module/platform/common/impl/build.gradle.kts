plugins {
    id("ohmega-platform-impl")
    alias(libs.plugins.platform.common.spongeGradle)
}

minecraft {
    version(libs.versions.minecraft.asProvider().get())
    accessWideners(accessWidenerFile(apiProject()))
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.2")
    compileOnly(libs.common.misc.asm)
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.2")
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
    }
}
