dependencies {
    api(Libs.frocksdbjni)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_io)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-statebackend-rocksdb"
