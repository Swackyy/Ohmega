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
    // Due to focusing on project maintainability and Forge's stubbornness to use Fabric's Mixin fork, the original Mixin needs to be used here.
    // Slightly annoying for writing 'IMixinConfigPlugin's but it is what it is
    compileOnly("org.spongepowered:mixin:0.8.7")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.2")
    compileOnly(libs.common.misc.asm)
    compileOnly(libs.common.misc.asm.tree)
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.2")
}
