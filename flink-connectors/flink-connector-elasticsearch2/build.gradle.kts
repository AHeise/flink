dependencies {
    api(project(":flink-connectors:flink-connector-elasticsearch-base"))

    implementation(Libs.elasticsearch)
    implementation(project(":flink-streaming-java"))

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-elasticsearch-base", configuration = "testArtifacts"))
}

description = "flink-connector-elasticsearch2"

flinkForceDependencyVersion(name = "org.elasticsearch", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.client", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.plugin", version = project.property("elasticsearch.version"))