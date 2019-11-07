dependencies {
    api(Libs.nifi_site_to_site_client)
    api(project(":flink-streaming-java"))

    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(Libs.slf4j_log4j12)
}

description = "flink-connector-nifi"
