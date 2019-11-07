dependencies {
    api(project(":flink-connectors:flink-connector-kafka-base")) {
        exclude(group = "org.apache.kafka", module = "kafka-clients")
    }
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-shaded-curator"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.kafka_2_11)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-connectors:flink-connector-kafka-base", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(Libs.curator_test)
    testImplementation(Libs.commons_io)
    testImplementation(Libs.commons_collections)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-connector-kafka-0.8"

flinkForceDependencyVersion(group = "org.apache.kafka", version = project.property("kafka.version"))

tasks.withType<ShadowJar> {
    //  IMPORTANT: This must be kept in sync with flink-runtime
    relocate("org.apache.curator", "org.apache.flink.shaded.curator.org.apache.curator")
}