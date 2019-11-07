dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(Libs.jsr305)

    testImplementation(Libs.junit)
}

description = "flink-dataset-fine-grained-recovery-test"

flinkSetMainClass("org.apache.flink.batch.tests.DataSetFineGrainedRecoveryTestProgram")