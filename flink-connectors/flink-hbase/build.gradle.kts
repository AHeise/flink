dependencies {
    api(Libs.flink_shaded_hadoop_2)
    api(Libs.hbase_client)

    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-streaming-java"))
    // TODO: gradle, does not seem to be used at all
//    implementation(Libs.hbase_server)

    testImplementation(project(":flink-clients"))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-scala"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-planner-blink", configuration = "testArtifacts"))
    testImplementation("${Libs.hbase_server}:tests")
    testImplementation(Libs.hadoop_minicluster)
    testImplementation(Libs.hbase_hadoop_compat)
    testImplementation(Libs.hadoop_hdfs)
    testImplementation(Libs.hbase_hadoop2_compat)
    testImplementation(Libs.hbase_server)
}

description = "flink-hbase"
