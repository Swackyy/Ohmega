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
val Project.commonUtilitySparkEnabledRuntime: String get() = getProperty("common.utility.spark.enabled.runtime")
val Project.compatFiguraEnabled: String get() = getProperty("compat.figura.enabled")
val Project.compatFiguraEnabledRuntime: String get() = getProperty("compat.figura.enabled.runtime")
val Project.compatJeiEnabled: String get() = getProperty("compat.jei.enabled")
val Project.compatJeiEnabledRuntime: String get() = getProperty("compat.jei.enabled.runtime")
val Project.compatPolymerEnabled: String get() = getProperty("compat.polymer.enabled")
val Project.compatPolymerEnabledRuntime: String get() = getProperty("compat.polymer.enabled.runtime")
val Project.compatReiEnabled: String get() = getProperty("compat.rei.enabled")
val Project.compatReiEnabledRuntime: String get() = getProperty("compat.rei.enabled.runtime")
val Project.compatRrvEnabled: String get() = getProperty("compat.rrv.enabled")
val Project.compatRrvEnabledRuntime: String get() = getProperty("compat.rrv.enabled.runtime")
val Project.compatTrinketsEnabled: String get() = getProperty("compat.trinkets.enabled")
val Project.compatTrinketsEnabledRuntime: String get() = getProperty("compat.trinkets.enabled.runtime")
val Project.modAuthors: String get() = getProperty("mod.authors")
val Project.modChanges: String get() = getProperty("mod.changes")
val Project.modDescriptionSectionCrowdin: String get() = getProperty("mod.description.section.crowdin")
val Project.modDescriptionSectionDiscord: String get() = getProperty("mod.description.section.discord")
val Project.modDescriptionSectionSource: String get() = getProperty("mod.description.section.source")
val Project.modDescriptionSectionWiki: String get() = getProperty("mod.description.section.wiki")
val Project.modDescriptionSummary: String get() = getProperty("mod.description.summary")
val Project.modId: String get() = getProperty("mod.id")
val Project.modLicense: String get() = getProperty("mod.license")
val Project.modName: String get() = getProperty("mod.name")
val Project.modUrlCrowdin: String get() = getProperty("mod.url.crowdin")
val Project.modUrlCurseforge: String get() = getProperty("mod.url.curseforge")
val Project.modUrlDiscord: String get() = getProperty("mod.url.discord")
val Project.modUrlGithub: String get() = getProperty("mod.url.github")
val Project.modUrlModrinth: String get() = getProperty("mod.url.modrinth")
val Project.modUrlWiki: String get() = getProperty("mod.url.wiki")
val Project.platformFabricUtilityModMenuEnabledRuntime: String get() = getProperty("platform.fabric.utility.modMenu.enabled.runtime")

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

private fun Project.getProperty(name: String): String {
    val property = findProperty(name)

    if (property != null) {
        return property.toString()
    }

    throw IllegalStateException("Queried gradle.properties property is not defined")
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