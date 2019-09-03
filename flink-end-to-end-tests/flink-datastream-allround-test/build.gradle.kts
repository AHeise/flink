plugins {
    id("com.commercehub.gradle.plugin.avro") version "0.15.1"
}

dependencies {
    implementation(project(":flink-formats:flink-avro"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
}

description = "flink-datastream-allround-test"
