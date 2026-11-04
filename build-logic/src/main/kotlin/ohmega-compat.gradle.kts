plugins {
    id("ohmega-module")
    alias(libs.plugins.platform.common.spongeGradle)
}

// There are no Javadocs (or not supposed to be) in compat modules,
// so we don't need this causing random annoying issues
tasks.named ("javadoc") {
    enabled = false
}

val commonImplProject: Project = commonImplProject()

// Remove when project isolation viable
evaluationDependsOn(commonImplProject.path)

minecraft {
    version(libs.versions.minecraft.asProvider().get())
    accessWideners(accessWidenerFile(commonImplProject.apiProject()))
}

repositories {
    maven {
        name = "Sponge"
        url = uri("https://repo.spongepowered.org/repository/maven-public")
    }
}

dependencies {
    apiModuleDependency(this, commonImplProject.apiProject())
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.2")
    compileOnly("org.ow2.asm:asm:9.9.1")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.2")
}
