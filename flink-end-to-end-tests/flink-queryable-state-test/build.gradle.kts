dependencies {
    implementation(Libs.flink_shaded_guava)
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
}

description = "flink-queryable-state-test"
