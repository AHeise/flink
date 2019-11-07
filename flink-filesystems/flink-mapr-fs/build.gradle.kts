dependencies {

    implementation(project(":flink-core"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(Libs.flink_shaded_hadoop_2)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(Libs.slf4j_log4j12)
}

description = "flink-mapr-fs"
