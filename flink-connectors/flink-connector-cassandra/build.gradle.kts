dependencies {
    api(Libs.cassandra_driver_core)

    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.cassandra_driver_mapping)
    implementation(Libs.flink_shaded_guava)

    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    testImplementation(Libs.cassandra_all)
}

description = "flink-connector-cassandra"
