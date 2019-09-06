dependencies {
    implementation(Libs.slf4j_api)
    api(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    compileOnly(project(":flink-annotations"))
    testImplementation(project(path = ":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    implementation(project(":flink-core"))
}

description = "flink-queryable-state-client-java"
