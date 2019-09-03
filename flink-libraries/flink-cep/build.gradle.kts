dependencies {
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_lang3)
    implementation(project(":flink-java"))
    implementation(project(":flink-core"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
}

description = "flink-cep"
