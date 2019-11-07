dependencies {
    api(project(":flink-connectors:flink-connector-kafka-0.9"))

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.kafka_clients)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-kafka-base", configuration = TEST_JAR))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(Libs.kafka_2_11)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.commons_collections)
    testImplementation(Libs.commons_io)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-connector-kafka-0.10"

flinkForceDependencyVersion(group = "org.apache.kafka", version = project.property("kafka.version"))