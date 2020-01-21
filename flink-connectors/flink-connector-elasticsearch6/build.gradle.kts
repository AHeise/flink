dependencies {
    api(project(":flink-connectors:flink-connector-elasticsearch-base"))
    api(Libs.elasticsearch_rest_high_level_client)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.log4j_to_slf4j version "2.9.1")
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-elasticsearch-base", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(project(":flink-formats:flink-json"))
    testImplementation(Libs.transport)
    testImplementation(Libs.transport_netty4_client)
    testImplementation(Libs.log4j_core)
}

description = "flink-connector-elasticsearch6"

flinkDependencyManagement {
    dependencyGroup(stringProperty("elasticsearch.version")) {
        dependency(Libs.elasticsearch)
        dependency(Libs.elasticsearch_rest_high_level_client)
        dependency(Libs.transport)
        dependency(Libs.transport_netty4_client)
    }
}