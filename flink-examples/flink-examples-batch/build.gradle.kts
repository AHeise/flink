dependencies {
    implementation(project(":flink-java"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-clients"))
    implementation(Libs.commons_io)
    implementation(Libs.commons_lang3)
}

description = "flink-examples-batch"
