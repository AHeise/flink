dependencies {
    implementation(project(":flink-core"))
    
    shade(project(":flink-filesystems:flink-s3-fs-base"))
    shade(project(":flink-filesystems:flink-hadoop-fs"))
    shade(Libs.hadoop_aws)
    shade(Libs.hadoop_common)
    shade(Libs.aws_java_sdk_core)
    shade(Libs.aws_java_sdk_s3)
    shade(Libs.aws_java_sdk_kms)
    shade(Libs.aws_java_sdk_dynamodb)

    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
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