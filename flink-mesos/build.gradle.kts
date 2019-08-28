plugins {
    id("scala")
}

dependencies {
    implementation(Libs.mesos)
    implementation(Libs.fenzo_core)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.grizzled_slf4j_2_11)
    testImplementation(Libs.scalatest_2_11)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.akka_testkit_2_11)
    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    compileOnly(project(":flink-runtime"))
    compileOnly(project(":flink-clients"))
    compileOnly(Libs.flink_shaded_jackson)
    compileOnly(Libs.akka_actor_2_11)
    compileOnly(Libs.akka_remote_2_11)
    compileOnly(Libs.akka_slf4j_2_11)
}

description = "flink-mesos"

flinkJointScalaJavaCompilation()
