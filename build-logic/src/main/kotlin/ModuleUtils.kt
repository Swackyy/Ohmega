@file:Suppress("UNCHECKED_CAST")

import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.project
import java.io.File
import kotlin.apply

// Non-libs properties
val Project.commonUtilitySparkEnabledRuntime: String get() = providers.gradleProperty("common.utility.spark.enabled.runtime").get()
val Project.compatFiguraEnabled: String get() = providers.gradleProperty("compat.figura.enabled").get()
val Project.compatFiguraEnabledRuntime: String get() = providers.gradleProperty("compat.figura.enabled.runtime").get()
val Project.compatJeiEnabled: String get() = providers.gradleProperty("compat.jei.enabled").get()
val Project.compatJeiEnabledRuntime: String get() = providers.gradleProperty("compat.jei.enabled.runtime").get()
val Project.compatPolymerEnabled: String get() = providers.gradleProperty("compat.polymer.enabled").get()
val Project.compatPolymerEnabledRuntime: String get() = providers.gradleProperty("compat.polymer.enabled.runtime").get()
val Project.compatReiEnabled: String get() = providers.gradleProperty("compat.rei.enabled").get()
val Project.compatReiEnabledRuntime: String get() = providers.gradleProperty("compat.rei.enabled.runtime").get()
val Project.compatRrvEnabled: String get() = providers.gradleProperty("compat.rrv.enabled").get()
val Project.compatRrvEnabledRuntime: String get() = providers.gradleProperty("compat.rrv.enabled.runtime").get()
val Project.compatTrinketsEnabled: String get() = providers.gradleProperty("compat.trinkets.enabled").get()
val Project.compatTrinketsEnabledRuntime: String get() = providers.gradleProperty("compat.trinkets.enabled.runtime").get()
val Project.modAuthors: String get() = providers.gradleProperty("mod.authors").get()
val Project.modChanges: String get() = providers.gradleProperty("mod.changes").get()
val Project.modDescriptionSectionCrowdin: String get() = providers.gradleProperty("mod.description.section.crowdin").get()
val Project.modDescriptionSectionDiscord: String get() = providers.gradleProperty("mod.description.section.discord").get()
val Project.modDescriptionSectionSource: String get() = providers.gradleProperty("mod.description.section.source").get()
val Project.modDescriptionSectionWiki: String get() = providers.gradleProperty("mod.description.section.wiki").get()
val Project.modDescriptionSummary: String get() = providers.gradleProperty("mod.description.summary").get()
val Project.modId: String get() = providers.gradleProperty("mod.id").get()
val Project.modLicense: String get() = providers.gradleProperty("mod.license").get()
val Project.modName: String get() = providers.gradleProperty("mod.name").get()
val Project.modUrlCrowdin: String get() = providers.gradleProperty("mod.url.crowdin").get()
val Project.modUrlCurseforge: String get() = providers.gradleProperty("mod.url.curseforge").get()
val Project.modUrlDiscord: String get() = providers.gradleProperty("mod.url.discord").get()
val Project.modUrlGithub: String get() = providers.gradleProperty("mod.url.github").get()
val Project.modUrlModrinth: String get() = providers.gradleProperty("mod.url.modrinth").get()
val Project.modUrlWiki: String get() = providers.gradleProperty("mod.url.wiki").get()
val Project.platformFabricUtilityModMenuEnabledRuntime: String get() = providers.gradleProperty("platform.fabric.utility.modMenu.enabled.runtime").get()

fun Project.apiModuleDependency(handler: DependencyHandler, dependencyProject: Project) {
    val dependencyPath: String = dependencyProject.path
    val dependency: ProjectDependency = handler.project(dependencyPath)
    // Possibly unneeded
    val fixedGroup: String = group.toString()

    handler.add ("api", dependency) {
        capabilities {
            requireCapability("$fixedGroup:${modId}-${dependencyProject.name}")
        }
    }

    includeProjectOutput(handler, dependencyPath)
}

fun includeProjectOutput(handler: DependencyHandler, dependencyPath: String) {
    handler.add("commonJava", handler.project(dependencyPath, "commonJava"))
    handler.add("commonResources", handler.project(dependencyPath, "commonResources"))
}

fun Project.accessWidenerFile(project: Project): File {
    return project.file("src/main/resources/${metaFileName(project)}.accesswidener")
}

fun Project.commonImplProject(): Project {
    return platformImplProject("common")
}

fun Project.compatModuleDependency(handler: DependencyHandler, name: String) {
    val dependencyProject: Project = compatProject(name)
    val dependencyPath: String = dependencyProject.path
    val dependency: ProjectDependency = handler.project(dependencyPath)
    // Possibly unneeded
    val fixedGroup: String = group.toString()

    handler.add ("implementation", dependency) {
        capabilities {
            requireCapability("$fixedGroup:$modId-${dependencyProject.name}")
        }
    }

    includeProjectOutput(handler, dependencyPath)
}

fun Project.compatProject(name: String): Project {
    return rootProject.project("module:compat:$name")
}

fun Project.metaFileName(project: Project): String {
    return "${modId}_${project.name}"
}

fun Project.mixinConfigName(project: Project): String {
    return "${metaFileName(project)}.mixins.json"
}

fun Project.moduleDependency(handler: DependencyHandler, dependencyProject: Project) {
    val dependencyPath: String = dependencyProject.path
    val dependency: ProjectDependency = handler.project(dependencyPath)
    // Possibly unneeded
    val fixedGroup: String = group.toString()

    handler.add ("implementation", dependency) {
        capabilities {
            requireCapability("$fixedGroup:$modId-${dependencyProject.name}")
        }
    }

    includeProjectOutput(handler, dependencyPath)
}

fun Project.platformProject(name: String): Project {
    return rootProject.project("module:platform:$name")
}

fun Project.platformApiProject(name: String): Project {
    return platformProject("$name:$name-api")
}

fun Project.platformImplProject(name: String): Project {
    return platformProject("$name:$name")
}

fun Project.apiProject(): Project {
    val parent: Project? = project.parent

    if (parent != null) {
        return parent.project("${parent.name}-api")
    }

    throw IllegalStateException("Project has no parent")
}

fun Project.addExtraMixinConfig(targetProject: Project, configName: String) {
    targetProject.afterEvaluate {
        if (targetProject.extra.has("ohmegaPlatformExtraMixinConfigs")) {
            targetProject.extra["ohmegaPlatformExtraMixinConfigs"].apply {
                (this as MutableList<String>).add(configName)
            }
        }
    }
}