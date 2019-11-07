dependencies {
    implementation(project(":flink-java"))
    implementation(project(":flink-core"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(project(":flink-streaming-java"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_lang3)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-cep"
