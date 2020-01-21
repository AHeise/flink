dependencies {
    api(project(":flink-ml-parent:flink-ml-api"))

    implementation(project(":flink-core"))
    implementation(Libs.commons_lang3)
    implementation(Libs.netlib_core version "1.1.2")
    testImplementation(Libs.junit)
}
