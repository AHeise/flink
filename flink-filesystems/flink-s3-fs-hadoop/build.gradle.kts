dependencies {
    implementation(project(":flink-filesystems:flink-s3-fs-base"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(Libs.hadoop_aws)
    implementation(Libs.hadoop_common)
    implementation(Libs.aws_java_sdk_core)
    implementation(Libs.aws_java_sdk_s3)
    implementation(Libs.aws_java_sdk_kms)
    implementation(Libs.aws_java_sdk_dynamodb)
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    implementation(project(":flink-core"))
}

description = "flink-s3-fs-hadoop"
