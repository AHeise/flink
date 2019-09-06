description = "flink-metrics-core"

dependencies {
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.junit)
}
flinkCreateTestJar()
