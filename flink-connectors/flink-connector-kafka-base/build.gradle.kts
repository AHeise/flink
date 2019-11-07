dependencies {
    api(Libs.kafka_clients)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_collections)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests", configuration = TEST_JAR))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(Libs.hadoop_minikdc)
    testImplementation(Libs.kafka_2_11)
    testImplementation(Libs.zkclient)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-connector-kafka-base"

flinkCreateTestJar()