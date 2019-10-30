dependencies {
    implementation(Libs.flink_shaded_guava)
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
}

description = "flink-queryable-state-test"

flinkSetMainClass("org.apache.flink.streaming.tests.queryablestate.QsStateProducer")

val testJar by tasks.register<ShadowJar>("QsStateClient") {
    configurations = listOf(project.configurations["implementation"])

    include("org.apache.flink:flink-queryable-state-test*")

    archiveClassifier.set("QsStateClient")

    manifest {
        attributes(mapOf("Main-Class" to "org.apache.flink.streaming.tests.queryablestate.QsStateClient"))
    }
}