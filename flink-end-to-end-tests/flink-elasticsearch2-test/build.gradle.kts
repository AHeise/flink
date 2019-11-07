dependencies {
    implementation(project(":flink-connectors:flink-connector-elasticsearch2", configuration = SHADED))
    implementation(project(":flink-streaming-java"))
}

description = "flink-elasticsearch2-test"

flinkSetMainClass("org.apache.flink.streaming.tests.Elasticsearch2SinkExample")