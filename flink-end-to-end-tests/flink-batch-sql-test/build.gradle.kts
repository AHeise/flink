dependencies {
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-java"))
    implementation(project(":flink-streaming-java"))
}

description = "flink-batch-sql-test"

flinkSetMainClass("org.apache.flink.sql.tests.BatchSQLTestProgram")