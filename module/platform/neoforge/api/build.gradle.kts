plugins {
    id("ohmega-loader-api")
    alias(libs.plugins.platform.neoforge.mdg)
}

neoForge {
    version = libs.versions.platform.neoforge.asProvider().get()
    validateAccessTransformers = true

    accessTransformers {
        val file: File = file("src/main/resources/META-INF/accesstransformer.cfg")

        from(file)
        publish(file)
    }
}