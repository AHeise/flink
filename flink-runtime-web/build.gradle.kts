dependencies {
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.javassist)
    implementation(Libs.commons_io)
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(Libs.curator_test)
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(Libs.flink_shaded_jackson_module_jsonschema)
    testImplementation(Libs.scala_library)
}

description = "flink-runtime-web"

// TODO: gradle test
flinkCreateTestJar(mainClass = "org.apache.flink.runtime.webmonitor.handlers.utils.TestProgram",
        artifactName = "test-program") {
    include("org/apache/flink/runtime/webmonitor/handlers/utils/TestProgram.java")
}
flinkCreateTestJar(mainClass = "org.apache.flink.runtime.webmonitor.handlers.utils.TestProgram",
        artifactName = project.property("test.parameterProgram.name").toString()) {
    include("org/apache/flink/runtime/webmonitor/handlers/utils/TestProgram.java")
}
flinkCreateTestJar(artifactName = project.property("test.ParameterProgramNoManifest.name").toString()) {
    include("org/apache/flink/runtime/webmonitor/handlers/utils/TestProgram.java")
}