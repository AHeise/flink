plugins {
    id("scala")
}

dependencies {
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.scala_library)

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-optimizer", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime-web"))
    testImplementation(project(":flink-clients"))
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-scala"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-examples:flink-examples-batch"))
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(project(":flink-optimizer"))
    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(Libs.flink_shaded_hadoop_2)
    testImplementation(Libs.flink_shaded_jackson)
    testImplementation(Libs.flink_shaded_netty)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.scalatest_2_11)
    testImplementation(Libs.akka_testkit_2_11)
    testImplementation(Libs.joda_time)
    testImplementation(Libs.joda_convert)
    testImplementation(Libs.oshi_core)
    testImplementation(Libs.reflections)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-tests"

flinkCreateTestJar()
flinkJointScalaJavaCompilation()

// TODO: gradle create jars for the different test programs