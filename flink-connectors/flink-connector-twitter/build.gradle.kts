dependencies {
    api(Libs.hbc_core version stringProperty("hbc-core.version"))

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-java"))
}

description = "flink-connector-twitter"

tasks.withType<ShadowJar> {
    relocate("com.google", "org.apache.flink.twitter.shaded.com.google") {
        exclude("com.google.protobuf.**")
        exclude("com.google.inject.**")
    }
}