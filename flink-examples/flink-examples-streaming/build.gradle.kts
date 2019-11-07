plugins {
    scala
}

dependencies {
    api(project(":flink-streaming-scala"))
    api(project(":flink-streaming-java"))

    implementation(project(":flink-connectors:flink-connector-twitter"))
    implementation(project(":flink-connectors:flink-connector-kafka"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_io)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
}

description = "flink-examples-streaming"

//flinkJointScalaJavaCompilation()