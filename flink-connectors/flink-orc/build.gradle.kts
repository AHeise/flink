dependencies {
    api(project(":flink-core"))
    api(Libs.orc_core)
    api(Libs.flink_shaded_hadoop_2)

    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-clients"))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-orc"
