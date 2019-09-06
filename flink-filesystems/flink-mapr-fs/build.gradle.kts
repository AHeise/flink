dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(Libs.flink_shaded_hadoop_2)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
}

description = "flink-mapr-fs"
