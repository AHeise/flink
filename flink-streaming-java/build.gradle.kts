flinkCreateTestJar()

dependencies {
    api(project(":flink-core"))
    api(project(":flink-optimizer"))

    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_math3)
    implementation(Libs.commons_io)
    implementation(Libs.commons_lang3)
    implementation(Libs.scala_library)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testApi(project(":flink-runtime", configuration = TEST_JAR))

    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.powermock_module_junit4)
}

description = "flink-streaming-java"