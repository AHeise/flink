dependencies {
    compileOnly(project(":flink-annotations"))
    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))

    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-runtime", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
}

description = "flink-metrics-jmx"
