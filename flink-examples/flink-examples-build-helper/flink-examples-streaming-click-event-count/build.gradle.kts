dependencies {
    implementation(project(":flink-examples:flink-examples-streaming"))
}

description = "flink-examples-streaming-click-event-count"

tasks.withType<ShadowJar> {
    // org.apache.kafka:*
    exclude("LICENSE")
    // Does not contain anything relevant. Cites a binary dependency on jersey, but this is neither reflected in the dependency graph, nor are any jersey files bundled.
    exclude("NOTICE")
}

flinkSetMainClass("org.apache.flink.streaming.examples.windowing.clickeventcount.ClickEventCount")