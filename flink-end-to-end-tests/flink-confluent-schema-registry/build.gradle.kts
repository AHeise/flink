plugins {
    id("com.commercehub.gradle.plugin.avro") version "0.16.0"
}

repositories {
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    implementation(project(":flink-connectors:flink-connector-kafka-0.10"))
    implementation(project(":flink-formats:flink-avro"))
    implementation(project(":flink-formats:flink-avro-confluent-registry"))
    implementation(project(":flink-streaming-java"))
}

flinkSetMainClass("org.apache.flink.schema.registry.test.TestAvroConsumerConfluent")