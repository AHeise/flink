dependencies {
    implementation(project(":flink-end-to-end-tests:flink-datastream-allround-test"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    implementation(project(":flink-streaming-java"))
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
}

description = "flink-stream-state-ttl-test"

flinkSetMainClass("org.apache.flink.streaming.tests.DataStreamStateTTLTestProgram")