dependencies {
    implementation(Libs.commons_lang3)
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
}

description = "flink-local-recovery-and-allocation-test"
