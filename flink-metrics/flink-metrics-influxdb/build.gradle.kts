dependencies {
    api(project(":flink-core"))

    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.influxdb_java)
    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.wiremock)
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-metrics-influxdb"

tasks.withType<ShadowJar> {
    relocate("org.influxdb", "org.apache.flink.metrics.influxdb.shaded.org.influxdb")
    relocate("com.squareup.moshi", "org.apache.flink.metrics.influxdb.shaded.com.squareup.moshi")
    relocate("okhttp3", "org.apache.flink.metrics.influxdb.shaded.okhttp3")
    relocate("okio", "org.apache.flink.metrics.influxdb.shaded.okio")
    relocate("retrofit2", "org.apache.flink.metrics.influxdb.shaded.retrofit2")
}