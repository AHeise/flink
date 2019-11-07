dependencies {
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(Libs.flink_shaded_hadoop_2)
    implementation(Libs.commons_lang3)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)
    testImplementation(Libs.slf4j_log4j12)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation("${Libs.hadoop_hdfs}:tests")
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.mockito_core)
}

description = "flink-yarn"

flinkCreateTestJar()