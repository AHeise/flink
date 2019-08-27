dependencies {
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    implementation(Libs.hadoop_azure)
    implementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.azure)
    testImplementation(project(path = ":flink-core", configuration = "testArtifacts"))
    compileOnly(project(":flink-core"))
}

description = "flink-azure-fs-hadoop"
