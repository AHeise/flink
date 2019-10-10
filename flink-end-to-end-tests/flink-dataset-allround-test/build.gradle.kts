dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
}

description = "flink-dataset-allround-test"

flinkSetMainClass("org.apache.flink.batch.tests.DataSetAllroundTestProgram")