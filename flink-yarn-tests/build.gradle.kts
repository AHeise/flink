dependencies {
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-dist"))
    testImplementation(project(":flink-clients", configuration = "testArtifacts"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-yarn", configuration = "testArtifacts"))
    testImplementation(project(":flink-examples:flink-examples-batch"))
    testImplementation(project(":flink-examples:flink-examples-streaming"))
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.hadoop_yarn_client)
    testImplementation(Libs.hadoop_yarn_api)
    testImplementation(Libs.hadoop_minicluster)
    testImplementation(Libs.hadoop_minikdc)
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.curator_test)
}

description = "flink-yarn-tests"
