dependencies {
    api(project(":flink-connectors:flink-connector-kafka"))
}

description = "flink-sql-connector-kafka"

tasks.withType<ShadowJar> {
    // org.apache.kafka:*
    exclude("kafka/kafka-version.properties")
    exclude("LICENSE")
    // Does not contain anything relevant. Cites a binary dependency on jersey, but this is neither reflected in the
    // dependency graph, nor are any jersey files bundled.
    exclude("NOTICE")
    exclude("common/**")

    relocate("org.apache.kafka", "org.apache.flink.kafka.shaded.org.apache.kafka")
}