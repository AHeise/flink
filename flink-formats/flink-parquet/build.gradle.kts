plugins {
    id("com.commercehub.gradle.plugin.avro") version "0.15.1"
}

dependencies {
    api(project(":flink-core"))
    api(project(":flink-java"))
    api(project(":flink-table:flink-table-common"))
    api(Libs.parquet_avro)
    api(Libs.parquet_hadoop)
    implementation(Libs.flink_shaded_hadoop_2)

    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.fastutil)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-formats:flink-avro"))
    testImplementation(Libs.flink_shaded_guava)
}

description = "flink-parquet"

tasks.withType<com.commercehub.gradle.plugin.avro.GenerateAvroJavaTask>().named("generateTestAvroJava") {
    setSource(file("src/test/resources/avro"))
    stringType = "CharSequence"
}