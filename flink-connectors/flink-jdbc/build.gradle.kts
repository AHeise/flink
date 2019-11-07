dependencies {
    api(project(":flink-streaming-java"))

    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(Libs.derby)
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
}

description = "flink-jdbc"
