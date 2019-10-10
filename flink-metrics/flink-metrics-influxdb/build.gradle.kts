dependencies {
    implementation(Libs.influxdb_java)
    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-runtime", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.wiremock)
    api(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))
}

description = "flink-metrics-influxdb"

tasks.withType<ShadowJar> {
    relocate("org.influxdb", "org.apache.flink.metrics.influxdb.shaded.org.influxdb")
    relocate("com.squareup.moshi", "org.apache.flink.metrics.influxdb.shaded.com.squareup.moshi")
    relocate("okhttp3", "org.apache.flink.metrics.influxdb.shaded.okhttp3")
    relocate("okio", "org.apache.flink.metrics.influxdb.shaded.okio")
    relocate("retrofit2", "org.apache.flink.metrics.influxdb.shaded.retrofit2")
}