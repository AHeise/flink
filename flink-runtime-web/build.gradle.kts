dependencies {
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.javassist)
    implementation(Libs.commons_io)
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(Libs.curator_test)
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(Libs.flink_shaded_jackson_module_jsonschema)
    testImplementation(Libs.scala_library)
}

description = "flink-runtime-web"
