plugins {
    id("scala")
}

dependencies {
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.akka_actor_2_11)
    implementation(Libs.akka_remote_2_11)
    implementation(Libs.akka_slf4j_2_11)
    implementation(Libs.mesos)
    implementation(Libs.fenzo_core)
    implementation(Libs.commons_cli)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.grizzled_slf4j_2_11)
    testImplementation(Libs.scalatest_2_11)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.akka_testkit_2_11)
    testImplementation(project(":flink-runtime", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
}

description = "flink-mesos"

flinkJointScalaJavaCompilation()
