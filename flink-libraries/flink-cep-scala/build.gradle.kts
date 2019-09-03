dependencies {
    implementation(project(":flink-libraries:flink-cep"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-streaming-scala"))
    testImplementation(project(":flink-libraries:flink-cep"))
    implementation(project(":flink-streaming-scala"))
    implementation(Libs.scala_reflect)
    implementation(Libs.scala_library)
    implementation(Libs.scala_compiler)
}

description = "flink-cep-scala"
