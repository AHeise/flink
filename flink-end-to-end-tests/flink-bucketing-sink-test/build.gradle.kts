dependencies {
    implementation(project(":flink-connectors:flink-connector-filesystem"))
    implementation(project(":flink-streaming-java"))
    implementation(Libs.flink_shaded_hadoop_2)
}

description = "flink-bucketing-sink-test"

flinkSetMainClass("org.apache.flink.streaming.tests.BucketingSinkTestProgram")