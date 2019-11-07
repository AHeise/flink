dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(project(":flink-table:flink-table-planner-blink"))
    implementation(project(":flink-table:flink-table-runtime-blink"))
    implementation(project(":flink-connectors:flink-connector-hive"))
    implementation(Libs.hadoop_mapreduce_client_core)
    implementation(Libs.jline_terminal)
    implementation(Libs.jline_reader)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_cli)

    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-clients", configuration = TEST_JAR))
    testImplementation(project(":flink-connectors:flink-connector-hive", configuration = TEST_JAR))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(Libs.hive_metastore)
    testImplementation(Libs.hive_exec)
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.mockito_core)
}

description = "flink-sql-client"

flinkExclude(group = "org.apache.calcite", name = "calcite-core")