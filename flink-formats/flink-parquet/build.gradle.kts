plugins {
    id("com.commercehub.gradle.plugin.avro") version "0.15.1"
}

dependencies {
    api(project(":flink-core"))
    api(project(":flink-java"))
    api(project(":flink-table:flink-table-common"))
    flinkDependencyGroup(version = stringProperty("flink.format.parquet.version")) {
        api(Libs.parquet_avro)
        api(Libs.parquet_hadoop)
    }

    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.flink_shaded_hadoop_2)
    implementation(Libs.fastutil version "8.2.1")
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-formats:flink-avro"))
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.mockito_core)
}

description = "flink-parquet"

tasks.withType<com.commercehub.gradle.plugin.avro.GenerateAvroJavaTask>().named("generateTestAvroJava") {
    setSource(file("src/test/resources/avro"))
    stringType = "CharSequence"
}