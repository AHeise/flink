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

tasks.withType<ShadowJar> {
    //  relocate the references to Hadoop to match the shaded Hadoop config
    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")
    //  relocate the AWS dependencies
    relocate("com.amazon", "org.apache.flink.fs.s3base.shaded.com.amazon")
    //  relocated S3 hadoop dependencies
    relocate("javax.xml.bind", "org.apache.flink.fs.s3hadoop.shaded.javax.xml.bind")
    //  shade Flink's Hadoop FS utility classes
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.s3hadoop.common")
}