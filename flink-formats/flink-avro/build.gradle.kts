plugins {
    id("com.commercehub.gradle.plugin.avro") version "0.15.1"
}

dependencies {
    api(Libs.avro)
    api(project(":flink-core"))

    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-table:flink-table-common"))
    implementation(Libs.joda_time)
    implementation(Libs.jsr305)
    implementation(Libs.kryo)

    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-api-java-bridge"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-scala", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
}

description = "flink-avro"

tasks.withType<com.commercehub.gradle.plugin.avro.GenerateAvroJavaTask>().named("generateTestAvroJava") {
    setSource(file("src/test/resources/avro"))
    stringType = "CharSequence"
    setEnableDecimalLogicalType("false")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("maven")
}