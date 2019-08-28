dependencies {
    implementation(Libs.simpleclient)
    implementation(Libs.simpleclient_httpserver)
    implementation(Libs.simpleclient_pushgateway)
    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.unirest_java)
    implementation(project(":flink-annotations"))
    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    testImplementation(project(":flink-core"))
}

description = "flink-metrics-prometheus"
