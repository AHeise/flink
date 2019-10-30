dependencies {
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-streaming-java"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.commons_cli)
    testImplementation(Libs.flink_shaded_guava)
}

description = "flink-container"

flinkSetMainClass("org.apache.flink.container.entrypoint.TestJob")

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("maven")
}