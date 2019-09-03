dependencies {
    api(Libs.hbc_core)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-java"))
}

description = "flink-connector-twitter"
