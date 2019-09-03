dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(Libs.flink_shaded_netty)

    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(Libs.curator_test)
    testImplementation(Libs.scala_library)
}

description = "flink-queryable-state-runtime"
