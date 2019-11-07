dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(Libs.irclib)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-connector-wikiedits"
