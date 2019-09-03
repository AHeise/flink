dependencies {
    api(Libs.elasticsearch)
    api(project(":flink-streaming-java"))
    api(project(":flink-table:flink-table-common"))

    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-common", configuration = "testArtifacts"))
    testImplementation(project(":flink-formats:flink-json"))
}

description = "flink-connector-elasticsearch-base"

flinkCreateTestJar()