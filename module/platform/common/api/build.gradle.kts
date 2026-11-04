plugins {
    id("ohmega-platform")
    alias(libs.plugins.platform.common.spongeGradle)
}

minecraft {
    version(libs.versions.minecraft.asProvider().get())
    accessWideners(accessWidenerFile(project))
}