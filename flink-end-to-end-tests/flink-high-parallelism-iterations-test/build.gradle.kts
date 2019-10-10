dependencies {
    implementation(project(":flink-examples:flink-examples-batch"))
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
}

description = "flink-high-parallelism-iterations-test"

flinkSetMainClass("org.apache.flink.batch.HighParallelismIterationsTestProgram")