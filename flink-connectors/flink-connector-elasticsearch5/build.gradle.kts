dependencies {
    api(project(":flink-connectors:flink-connector-elasticsearch-base"))

    implementation(Libs.transport)
    implementation(Libs.transport_netty3_client)
    implementation(Libs.log4j_to_slf4j)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-elasticsearch-base", configuration = "testArtifacts"))
    testImplementation(Libs.log4j_core)
}

description = "flink-connector-elasticsearch5"

flinkForceDependencyVersion(name = "org.elasticsearch", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.client", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.plugin", version = project.property("elasticsearch.version"))