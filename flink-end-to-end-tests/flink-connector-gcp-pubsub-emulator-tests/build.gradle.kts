dependencies {
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-gcp-pubsub"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-runtime"))
    testImplementation(Libs.docker_client)
    testImplementation(Libs.junit)
}

description = "flink-connector-gcp-pubsub-emulator-tests"
