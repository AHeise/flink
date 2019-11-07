dependencies {
    implementation(Libs.slf4j_api)
    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    compileOnly(project(":flink-annotations"))
    implementation(project(":flink-metrics:flink-metrics-core"))
}

description = "flink-metrics-statsd"
