dependencies {
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    implementation(Libs.hadoop_aliyun)
    implementation(Libs.aliyun_sdk_oss)
    testImplementation(project(path = ":flink-filesystems:flink-fs-hadoop-shaded", configuration = "testArtifacts"))
    testImplementation(project(path = ":flink-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":flink-filesystems:flink-hadoop-fs", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    implementation(project(":flink-core"))
}

tasks.withType<ShadowJar> {
    exclude(".gitkeep")
    exclude("mime.types")
    exclude("mozilla/**")
    exclude("META-INF/maven/**")


    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")
    //  relocate the OSS dependencies
    relocate("com.aliyun", "org.apache.flink.fs.osshadoop.shaded.com.aliyun")
    relocate("com.aliyuncs", "org.apache.flink.fs.osshadoop.shaded.com.aliyuncs")
}