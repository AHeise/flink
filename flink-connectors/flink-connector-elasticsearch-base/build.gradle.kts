dependencies {
    api(Libs.elasticsearch)
    api(project(":flink-streaming-java"))
    api(project(":flink-table:flink-table-common"))

    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(project(":flink-formats:flink-json"))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
}

description = "flink-connector-elasticsearch-base"

flinkCreateTestJar()