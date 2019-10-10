dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-java"))
    implementation(Libs.aws_java_sdk_s3)
}

flinkSetMainClass("org.apache.flink.streaming.tests.util.s3.S3UtilProgram")