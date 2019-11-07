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
    implementation(Libs.jsr305)

    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(Libs.scalatest_2_11)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.akka_testkit_2_11)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-mesos"

flinkJointScalaJavaCompilation()

tasks.withType<ShadowJar> {
    relocate("com.google.protobuf", "org.apache.flink.mesos.shaded.com.google.protobuf")
    relocate("com.fasterxml.jackson", "org.apache.flink.mesos.shaded.com.fasterxml.jackson")
}