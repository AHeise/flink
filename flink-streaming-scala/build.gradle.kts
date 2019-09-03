plugins {
    id("scala")
}

dependencies {
    api(Libs.scala_library)
    api(project(":flink-streaming-java"))
    api(project(":flink-scala"))

    implementation(project(":flink-java"))
    implementation(Libs.scala_reflect)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-tests", configuration = "testArtifacts"))
    testImplementation(Libs.scalatest_2_11)
}

description = "flink-streaming-scala"

flinkJointScalaJavaCompilation()

flinkCreateTestJar()