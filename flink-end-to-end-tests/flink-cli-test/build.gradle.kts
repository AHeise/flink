dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-java"))
}

description = "flink-cli-test"

flinkSetMainClass("org.apache.flink.graph.Runner")