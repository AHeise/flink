dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    implementation(project(":flink-dist"))
    implementation(Libs.okhttp)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.junit)
    implementation(Libs.commons_io)
}
