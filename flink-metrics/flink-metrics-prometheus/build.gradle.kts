dependencies {
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))

    shade(Libs.simpleclient)
    shade(Libs.simpleclient_httpserver)
    shade(Libs.simpleclient_pushgateway)

    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.unirest_java)
}

description = "flink-metrics-prometheus"

tasks.withType<ShadowJar> {
    relocate("io.prometheus.client", "org.apache.flink.shaded.io.prometheus.client")
}