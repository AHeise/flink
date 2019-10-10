dependencies {
    api(project(":flink-connectors:flink-connector-kafka-base")) {
        exclude(group = "org.apache.kafka", module = "kafka-clients")
    }
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-shaded-curator"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.kafka_2_11)

    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-connectors:flink-connector-kafka-base", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = "testArtifacts"))
    testImplementation(Libs.curator_test)
    testImplementation(Libs.commons_io)
    testImplementation(Libs.commons_collections)
}

description = "flink-connector-kafka-0.8"

flinkForceDependencyVersion(group = "org.apache.kafka", version = project.property("kafka.version"))

tasks.withType<ShadowJar> {
    //  IMPORTANT: This must be kept in sync with flink-runtime
    relocate("org.apache.curator", "org.apache.flink.shaded.curator.org.apache.curator")
}