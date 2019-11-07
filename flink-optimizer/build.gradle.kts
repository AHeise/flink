dependencies {
    api(project(":flink-runtime"))

    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(Libs.commons_lang3)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-optimizer"

flinkCreateTestJar()