dependencies {
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-metrics-jmx"
