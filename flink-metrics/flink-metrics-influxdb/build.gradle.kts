dependencies {
    implementation(Libs.influxdb_java)
    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-runtime", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.wiremock)
    api(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))
}

description = "flink-metrics-influxdb"
