dependencies {
    implementation(project(":flink-connectors:flink-connector-elasticsearch5"))
    implementation(project(":flink-streaming-java"))
}

description = "flink-elasticsearch5-test"

flinkSetMainClass("org.apache.flink.streaming.tests.Elasticsearch5SinkExample")