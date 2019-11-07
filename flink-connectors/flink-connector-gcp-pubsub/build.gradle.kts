dependencies {
    api(Libs.google_cloud_pubsub)
    api(project(":flink-streaming-java"))

    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-connector-gcp-pubsub"
