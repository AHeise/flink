plugins {
    scala
}

dependencies {
    api(Libs.flink_shaded_hadoop_2)
    api(project(":flink-core"))

    implementation(project(":flink-java"))
    implementation(project(":flink-scala"))

    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-java", configuration = "testArtifacts"))
}

description = "flink-hadoop-compatibility"
