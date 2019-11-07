dependencies {
    api(Libs.flink_shaded_hadoop_2)
    api(project(":flink-streaming-java"))

    implementation(project(":flink-formats:flink-avro"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(Libs.commons_lang3)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-tests", configuration = TEST_JAR))
    testImplementation("${Libs.hadoop_hdfs}:tests")
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.hadoop_minikdc)
}

description = "flink-connector-filesystem"
