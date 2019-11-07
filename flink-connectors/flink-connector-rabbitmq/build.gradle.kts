dependencies {
    api(Libs.amqp_client)
    api(project(":flink-streaming-java"))

    implementation(Libs.jsr305)

    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-connector-rabbitmq"
