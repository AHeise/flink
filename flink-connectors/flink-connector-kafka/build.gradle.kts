repositories {
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    api(project(":flink-connectors:flink-connector-kafka-base"))

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.kafka_clients)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_lang3)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-connectors:flink-connector-kafka-base", configuration = TEST_JAR))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(Libs.kafka_2_11)
    testImplementation(Libs.commons_io)
    testImplementation(Libs.commons_collections)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-connector-kafka"
