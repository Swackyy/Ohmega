plugins {
    id("ohmega-compat")
}

repositories {
    maven {
        name = "Nucleoid"
        url = uri("https://maven.nucleoid.xyz")
    }
}

dependencies {
    compatModuleDependency(this, "trinkets:trinkets-common")
    compileOnlyApi(libs.platform.common.compat.trinkets)
}

addExtraMixinConfig(platformImplProject("fabric"), mixinConfigName(compatProject("trinkets:trinkets-common")))
