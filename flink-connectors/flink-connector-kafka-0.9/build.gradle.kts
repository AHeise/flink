dependencies {
    api(project(":flink-connectors:flink-connector-kafka-base")) {
        exclude(group = "org.apache.kafka", module = "kafka_${Versions.baseScala}")
        exclude(group = "org.apache.kafka", module = "kafka-clients")
    }

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.kafka_clients)

    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-connectors:flink-connector-kafka-base", configuration = "testArtifacts"))
    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(Libs.hadoop_minikdc)
    testImplementation(Libs.kafka_2_11)
    testImplementation(Libs.curator_test)
}

description = "flink-connector-kafka-0.9"

flinkForceDependencyVersion(group = "org.apache.kafka", version = project.property("kafka.version"))