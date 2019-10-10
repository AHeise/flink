dependencies {
    api(project(":flink-connectors:flink-connector-kafka-0.9"))
}

description = "flink-sql-connector-kafka-0.9"

tasks.withType<ShadowJar> {
    // org.apache.kafka:*
    exclude("kafka/kafka-version.properties")
    exclude("LICENSE")
    // Does not contain anything relevant. Cites a binary dependency on jersey, but this is neither reflected in the
    // dependency graph, nor are any jersey files bundled.
    exclude("NOTICE")

    relocate("org.apache.kafka", "org.apache.flink.kafka09.shaded.org.apache.kafka")
}