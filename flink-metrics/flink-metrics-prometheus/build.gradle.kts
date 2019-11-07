dependencies {
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.slf4j_api)

    shade(Libs.simpleclient)
    shade(Libs.simpleclient_httpserver)
    shade(Libs.simpleclient_pushgateway)

    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.unirest_java)
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-metrics-prometheus"

tasks.withType<ShadowJar> {
    relocate("io.prometheus.client", "org.apache.flink.shaded.io.prometheus.client")
}