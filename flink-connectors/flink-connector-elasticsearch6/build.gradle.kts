dependencies {
    api(project(":flink-connectors:flink-connector-elasticsearch-base"))
    api(Libs.elasticsearch_rest_high_level_client)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.log4j_to_slf4j)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-elasticsearch-base", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(project(":flink-formats:flink-json"))
    testImplementation(Libs.transport)
    testImplementation(Libs.transport_netty4_client)
    testImplementation(Libs.log4j_core)
}

description = "flink-connector-elasticsearch6"

flinkForceDependencyVersion(name = "org.elasticsearch", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.client", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.plugin", version = project.property("elasticsearch.version"))