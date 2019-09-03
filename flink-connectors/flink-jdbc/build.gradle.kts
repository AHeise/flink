dependencies {
    api(project(":flink-streaming-java"))

    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.flink_shaded_guava)

    testImplementation(Libs.derby)
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = "testArtifacts"))
}

description = "flink-jdbc"
