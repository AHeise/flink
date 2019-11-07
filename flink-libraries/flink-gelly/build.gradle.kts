dependencies {
    implementation(project(":flink-java"))
    implementation(project(":flink-clients"))
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_math3)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-optimizer", configuration = TEST_JAR))
}

description = "flink-gelly"
