dependencies {
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-core"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.junit)
}
