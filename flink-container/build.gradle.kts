dependencies {
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-streaming-java"))
    compileOnly(project(":flink-runtime"))
    compileOnly(project(":flink-clients"))
    compileOnly(Libs.commons_cli)
}

description = "flink-container"
