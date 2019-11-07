dependencies {
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-runtime"))
    implementation(project(path = ":flink-runtime", configuration = TEST_JAR))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-metrics:flink-metrics-prometheus"))
    implementation(project(":flink-runtime-web"))
    implementation(project(":flink-yarn"))
    implementation(project(":flink-mesos"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_jackson_module_jsonschema)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.jsoup)
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.junit)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-docs"

tasks.test {
    systemProperty("rootDir", project.rootDir)
}