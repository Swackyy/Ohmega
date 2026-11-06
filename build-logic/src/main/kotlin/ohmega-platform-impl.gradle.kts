plugins {
    id("ohmega-platform")
}

// Remove when project isolation viable
evaluationDependsOn(apiProject().path)

dependencies {
    apiModuleDependency(this, apiProject())
}
