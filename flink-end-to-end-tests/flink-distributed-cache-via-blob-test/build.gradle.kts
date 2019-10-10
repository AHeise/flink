dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-java"))
}

description = "flink-distributed-cache-via-blob"

flinkSetMainClass("org.apache.flink.streaming.tests.DistributedCacheViaBlobTestProgram")