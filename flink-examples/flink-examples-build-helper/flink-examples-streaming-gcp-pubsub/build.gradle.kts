dependencies {
    implementation(project(":flink-connectors:flink-connector-gcp-pubsub"))
    implementation(project(":flink-examples:flink-examples-streaming"))
}

description = "flink-examples-streaming-gcp-pubsub"

tasks.withType<ShadowJar> {
    relocate("com.google", "org.apache.flink.streaming.examples.gcp.pubsub.shaded.com.google")
}

flinkSetMainClass("org.apache.flink.streaming.examples.gcp.pubsub.PubSubExample")

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("PubSub")
}