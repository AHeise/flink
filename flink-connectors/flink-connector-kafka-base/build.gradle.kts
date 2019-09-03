dependencies {
    api(Libs.kafka_clients)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_collections)

    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests", configuration = "testArtifacts"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-common", configuration = "testArtifacts"))
    testImplementation(Libs.hadoop_minikdc)
    testImplementation(Libs.kafka_2_11)
    testImplementation(Libs.zkclient)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.flink_shaded_guava)
}

description = "flink-connector-kafka-base"

flinkCreateTestJar()