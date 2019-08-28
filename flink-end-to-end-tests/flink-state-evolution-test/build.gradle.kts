dependencies {
    implementation(project(":flink-formats:flink-avro"))
    compileOnly(project(":flink-java"))
    compileOnly(project(":flink-streaming-java"))
    compile(Libs.avro)
}

description = "flink-state-evolution-test"
