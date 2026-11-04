plugins {
    id("ohmega-platform")
}

// Remove when project isolation viable
evaluationDependsOn(apiProject().path)

dependencies {
    apiModuleDependency(this, apiProject())
}

// todo: see if this can remain disabled
/*tasks.withType<JavaCompile>().configureEach {
    classpath += apiProject().sourceSets.getByName("main").output
}*/
