dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(":flink-java"))
    implementation(Libs.commons_cli)
    implementation(Libs.scala_library) // we need this because ClusterClient uses FiniteDuration
    implementation(Libs.flink_shaded_netty) // we need this because RestClusterClient uses netty classes
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(path = ":flink-runtime", configuration = "testArtifacts"))
}

description = "flink-clients"

flinkCreateTestJar()