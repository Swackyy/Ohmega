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

includeBuild("build-logic")

rootProject.name = getProperty("mod.name")

include("module:platform:common:api")
include("module:platform:common:impl")

val platformEnabledFabric = getProperty("platform.fabric.enabled").toBoolean()
val platformEnabledForge = getProperty("platform.forge.enabled").toBoolean()
val platformEnabledNeoforge = getProperty("platform.neoforge.enabled").toBoolean()

if (platformEnabledFabric) {
    include("module:platform:fabric:api")
    include("module:platform:fabric:impl")
}

if (platformEnabledForge) {
    include("module:platform:forge:api")
    include("module:platform:forge:impl")
}

if (platformEnabledNeoforge) {
    include("module:platform:neoforge:api")
    include("module:platform:neoforge:impl")
}

// change when Figura officially ports
if (getProperty("compat.figura.enabled").toBoolean() && platformEnabledFabric) {
    include("module:compat:figura:common")
    include("module:compat:figura:fabric")
}

if (getProperty("compat.jei.enabled").toBoolean() && (platformEnabledFabric || platformEnabledNeoforge)) {
    include("module:compat:util:exclusion-zone")
    include("module:compat:jei")
}

if (getProperty("compat.polymer.enabled").toBoolean() && platformEnabledFabric) {
    include("module:compat:polymer")
}

if (getProperty("compat.rei.enabled").toBoolean() && (platformEnabledFabric || platformEnabledNeoforge)) {
    include("module:compat:util:exclusion-zone")
    include("module:compat:rei:common")

    if (platformEnabledFabric) {
        include("module:compat:rei:fabric")
    }

    if (platformEnabledNeoforge) {
        include("module:compat:rei:neoforge")
    }
}

if (getProperty("compat.rrv.enabled").toBoolean() && (platformEnabledFabric || platformEnabledNeoforge)) {
    include("module:compat:util:exclusion-zone")
    include("module:compat:rrv:common")

    if (platformEnabledFabric) {
        include("module:compat:rrv:fabric")
    }

    if (platformEnabledNeoforge) {
        include("module:compat:rrv:neoforge")
    }
}

if (getProperty("compat.trinkets.enabled").toBoolean() && (platformEnabledFabric || platformEnabledNeoforge)) {
    include("module:compat:trinkets:common")

    if (platformEnabledFabric) {
        include("module:compat:trinkets:fabric")
    }

    if (platformEnabledNeoforge) {
        include("module:compat:trinkets:neoforge")
    }
}

gradle.settingsEvaluated {
    rootProject.children.forEach { child ->
        eval(child)
    }
}

fun getProperty(name: String): String {
    val extra: ExtraPropertiesExtension = settings.extra

    if (extra.has(name)) {
        return extra.get(name).toString()
    }

    return ""
}

fun eval(child: ProjectDescriptor) {
    val parent: ProjectDescriptor? = child.parent

    if (parent != null) {
        val grandparent: ProjectDescriptor? = parent.parent

        if (grandparent != null) {
            val grandparentName: String = grandparent.name

            if (grandparentName == "compat") {
                child.name = "${parent.name}-${child.name}"
            } else if (grandparentName == "platform") {
                val originalName: String = child.name
                val parentName: String = parent.name

                if (originalName == "api") {
                    child.name = "$parentName-$originalName"
                } else if (originalName == "impl") {
                    child.name = parentName
                }
            }
        }
    }

    child.children.forEach { subChild ->
        eval(subChild)
    }
}
