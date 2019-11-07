dependencies {
    implementation(project(":flink-connectors:flink-connector-gcp-pubsub"))
    implementation(project(":flink-examples:flink-examples-streaming"))
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
}

description = "flink-examples-streaming-gcp-pubsub"

tasks.withType<ShadowJar> {
    relocate("com.google", "org.apache.flink.streaming.examples.gcp.pubsub.shaded.com.google")
}

flinkSetMainClass("org.apache.flink.streaming.examples.gcp.pubsub.PubSubExample")

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("PubSub")
}