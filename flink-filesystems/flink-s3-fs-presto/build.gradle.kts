dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-filesystems:flink-s3-fs-base"))
    implementation(Libs.presto_hive)
    implementation(Libs.hadoop_apache2)
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-s3-fs-presto"
