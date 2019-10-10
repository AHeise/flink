dependencies {
    shade(Libs.flink_shaded_guava)
    shade(project(":flink-core"))
    shade(project(":flink-java"))
    shade(project(":flink-queryable-state:flink-queryable-state-client-java"))
    shade(project(":flink-streaming-java"))
    shade(project(":flink-state-backends:flink-statebackend-rocksdb"))
}

description = "flink-queryable-state-test"

flinkSetMainClass("org.apache.flink.streaming.tests.queryablestate.QsStateClient")

val testJar by tasks.register<ShadowJar>("QsStateProducer") {
    archiveClassifier.set("QsStateProducer")

    manifest {
        attributes(mapOf("Main-Class" to "org.apache.flink.streaming.tests.queryablestate.QsStateProducer"))
    }
}