dependencies {
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-streaming-java"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.commons_cli)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-container"

flinkSetMainClass("org.apache.flink.container.entrypoint.TestJob")

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("maven")
}