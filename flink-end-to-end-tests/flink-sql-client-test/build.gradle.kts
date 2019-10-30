dependencies {
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-formats:flink-avro"))
    implementation(project(":flink-formats:flink-json"))
    implementation(project(":flink-formats:flink-csv"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka-0.9"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka-0.10"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka-0.11"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka"))
    implementation(project(":flink-connectors:flink-sql-connector-elasticsearch6"))
}

description = "flink-sql-client-test"

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("SqlToolbox")
}