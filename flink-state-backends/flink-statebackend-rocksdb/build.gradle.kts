dependencies {
    api(Libs.frocksdbjni)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_io)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
}

description = "flink-statebackend-rocksdb"
