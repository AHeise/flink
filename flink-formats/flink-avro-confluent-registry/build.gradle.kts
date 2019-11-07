repositories {
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    api(project(":flink-formats:flink-avro"))
    api(Libs.kafka_schema_registry_client)

    implementation(Libs.jsr305)

    testImplementation(Libs.junit)
}

tasks.withType<ShadowJar> {
    relocate("com.fasterxml.jackson", "org.apache.flink.formats.avro.registry.confluent.shaded.com.fasterxml.jackson")
    relocate("org.apache.zookeeper", "org.apache.flink.formats.avro.registry.confluent.shaded.org.apache.zookeeper")
    relocate("org.apache.jute", "org.apache.flink.formats.avro.registry.confluent.shaded.org.apache.jute")
    relocate("org.I0Itec.zkclient", "org.apache.flink.formats.avro.registry.confluent.shaded.org.101tec")
}