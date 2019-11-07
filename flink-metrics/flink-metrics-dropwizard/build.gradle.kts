dependencies {
    compileOnly(project(":flink-annotations"))

    implementation(Libs.metrics_core)
    implementation(project(":flink-metrics:flink-metrics-core"))

    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-metrics-dropwizard"
