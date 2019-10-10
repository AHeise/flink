dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-java"))
}

flinkSetMainClass("StreamingFileSinkProgram")