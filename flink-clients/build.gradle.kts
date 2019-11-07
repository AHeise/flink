dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(":flink-java"))
    implementation(Libs.commons_cli)
    implementation(Libs.scala_library) // we need this because ClusterClient uses FiniteDuration
    implementation(Libs.flink_shaded_netty) // we need this because RestClusterClient uses netty classes
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-clients"

flinkCreateTestJar("org.apache.flink.client.testjar.WordCount")

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("maven")
}