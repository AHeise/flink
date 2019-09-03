dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(Libs.irclib)
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-connector-wikiedits"
