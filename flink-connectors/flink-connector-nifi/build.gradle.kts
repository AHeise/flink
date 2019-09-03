dependencies {
    api(Libs.nifi_site_to_site_client)
    api(project(":flink-streaming-java"))

    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
}

description = "flink-connector-nifi"
