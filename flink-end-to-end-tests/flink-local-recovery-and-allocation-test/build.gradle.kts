dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    implementation(Libs.commons_lang3)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
}

description = "flink-local-recovery-and-allocation-test"

flinkSetMainClass("org.apache.flink.streaming.tests.StickyAllocationAndLocalRecoveryTestJob")