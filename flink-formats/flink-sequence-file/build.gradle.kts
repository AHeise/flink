dependencies {
    api(project(":flink-core"))
    api(Libs.flink_shaded_hadoop_2)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
}

description = "flink-sequence-file"
