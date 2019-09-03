dependencies {
    api(Libs.amqp_client)
    api(project(":flink-streaming-java"))

    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
}

description = "flink-connector-rabbitmq"
