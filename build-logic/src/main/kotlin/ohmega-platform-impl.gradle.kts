plugins {
    id("ohmega-platform")
}

// Remove when project isolation viable
evaluationDependsOn(apiProject().path)

dependencies {
    apiModuleDependency(this, apiProject())
}

/*tasks.withType<JavaCompile>().configureEach {
    classpath += apiProject().sourceSets.getByName("main").output
}*/
