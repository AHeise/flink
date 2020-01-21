dependencies {
    api(Libs.curator_recipes version stringProperty("curator.version"))
}

description = "flink-shaded-curator"

tasks.withType<ShadowJar> {
    relocate("com.google.common", "org.apache.flink.curator.shaded.com.google.common")
}