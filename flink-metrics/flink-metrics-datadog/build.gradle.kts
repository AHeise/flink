dependencies {
    implementation(Libs.slf4j_api)
    implementation(Libs.okhttp)
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.flink_shaded_jackson)

    testImplementation(project(":flink-metrics:flink-metrics-core"))
    testImplementation(Libs.flink_shaded_jackson)
    testImplementation(Libs.junit)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-metrics-datadog"

tasks.withType<ShadowJar> {
    relocate("okhttp3", "org.apache.flink.shaded.okhttp3")
    relocate("okio", "org.apache.flink.shaded.okio")
}