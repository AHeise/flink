dependencies {
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-examples:flink-examples-batch"))
    testImplementation(project(":flink-formats:flink-avro"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-runtime", configuration = "testArtifacts"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation("${Libs.hadoop_hdfs}:tests")
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.flink_shaded_hadoop_2)
}

description = "flink-fs-tests"
