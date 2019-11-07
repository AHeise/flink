plugins {
    id("scala")
}

dependencies {
    // for some reasons scala compiler requires everything in a method signature to be in the classpath even though it's only used in private methods...
    api(Libs.flink_shaded_asm)
    api(Libs.commons_lang3)
    api(Libs.scala_library)

    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.slf4j_api)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.scala_reflect)
    implementation(Libs.scala_compiler)

    testImplementation(project(":flink-runtime"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(path = ":flink-core", configuration = TEST_JAR))
    testImplementation(Libs.scalatest_2_11)
    testImplementation(Libs.chill_2_11)
    testImplementation(Libs.joda_time)
    testImplementation(Libs.joda_convert)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-scala"

flinkJointScalaJavaCompilation()
