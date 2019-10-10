dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
}

description = "flink-dataset-fine-grained-recovery-test"

flinkSetMainClass("org.apache.flink.batch.tests.DataSetFineGrainedRecoveryTestProgram")