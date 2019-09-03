dependencies {
    implementation(project(":flink-libraries:flink-gelly"))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-clients"))
    implementation(Libs.scala_reflect)
    implementation(Libs.scala_library)
    implementation(Libs.scala_compiler)
}

description = "flink-gelly-scala"
