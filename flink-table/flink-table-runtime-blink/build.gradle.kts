dependencies {
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(Libs.janino)
    implementation(Libs.avatica_core)
    implementation(Libs.lz4_java)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_math3)
    implementation(Libs.scala_library)
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-libraries:flink-cep"))
}

description = "flink-table-runtime-blink"

flinkCreateTestJar()