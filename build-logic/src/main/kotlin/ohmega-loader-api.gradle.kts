plugins {
    id("ohmega-platform")
}

dependencies {
    apiModuleDependency(this, commonImplProject().apiProject())
}