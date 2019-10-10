dependencies {
    implementation(project(":flink-formats:flink-avro"))
    implementation(project(":flink-java"))
    implementation(project(":flink-streaming-java"))
    compile(Libs.avro)
}

description = "flink-state-evolution-test"

flinkSetMainClass("org.apache.flink.test.StatefulStreamingJob")