plugins {
    scala
}

dependencies {
    api(Libs.flink_shaded_hadoop_2)
    api(project(":flink-core"))

    implementation(project(":flink-java"))
    implementation(project(":flink-scala"))
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-java", configuration = TEST_JAR))
    testImplementation(Libs.mockito_core)
}

description = "flink-hadoop-compatibility"
