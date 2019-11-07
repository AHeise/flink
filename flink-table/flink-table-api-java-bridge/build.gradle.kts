dependencies {
    api(project(":flink-table:flink-table-api-java"))
    api(project(":flink-streaming-java"))

    implementation(project(":flink-java"))

    testImplementation(Libs.junit)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-table-api-java-bridge"
