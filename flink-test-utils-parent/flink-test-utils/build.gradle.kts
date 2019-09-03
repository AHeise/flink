dependencies {
    api(project(":flink-test-utils-parent:flink-test-utils-junit"))
    api(project(":flink-clients"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(path = ":flink-runtime", configuration = "testArtifacts"))
    implementation(project(":flink-streaming-java"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.curator_test)
    implementation(Libs.hadoop_minikdc)
    implementation(Libs.scala_library)
}

description = "flink-test-utils"
