dependencies {
    api(project(":flink-annotations"))

    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.commons_lang3)
    implementation(Libs.kryo)
    implementation(Libs.commons_collections)
    implementation(Libs.commons_compress)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_asm)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.commons_io)
    testImplementation(Libs.joda_time)
    testImplementation(Libs.joda_convert)
    testImplementation(Libs.flink_shaded_jackson)
    testImplementation(Libs.lombok)
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.hamcrest_all)
    testAnnotationProcessor(Libs.lombok)
}

description = "flink-core"

flinkCreateTestJar()
