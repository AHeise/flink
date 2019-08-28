dependencies {
    api(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_math3)
    implementation(Libs.commons_io)
    implementation(Libs.commons_lang3)
    implementation(Libs.scala_library)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.flink_shaded_netty)
    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-runtime"))
}

description = "flink-streaming-java"
