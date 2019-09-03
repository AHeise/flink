dependencies {
    api(Libs.flink_shaded_hadoop_2)
    api(project(":flink-streaming-java"))

    implementation(project(":flink-formats:flink-avro"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(Libs.commons_lang3)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-tests", configuration = "testArtifacts"))
    testImplementation("${Libs.hadoop_hdfs}:tests")
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.hadoop_minikdc)
}

description = "flink-connector-filesystem"
