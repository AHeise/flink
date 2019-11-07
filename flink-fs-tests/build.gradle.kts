dependencies {
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-examples:flink-examples-batch"))
    testImplementation(project(":flink-formats:flink-avro"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation("${Libs.hadoop_hdfs}:tests")
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.flink_shaded_hadoop_2)
    testImplementation(Libs.mockito_core)
}

description = "flink-fs-tests"
