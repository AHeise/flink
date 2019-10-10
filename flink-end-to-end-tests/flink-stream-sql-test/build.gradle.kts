dependencies {
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
}

description = "flink-stream-sql-test"

flinkSetMainClass("org.apache.flink.sql.tests.StreamSQLTestProgram")