dependencies {
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-metrics-slf4j"
