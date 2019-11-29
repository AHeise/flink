dependencies {
    api(Libs.flink_shaded_hadoop_2) {
        exclude(Libs.netty)
        exclude(Libs.log4j)
    }

    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)
    implementation(Libs.commons_lang3)

    testImplementation(project(path = ":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation("${Libs.hadoop_hdfs}:tests")
    testImplementation("${Libs.hadoop_common}:tests") {
        exclude("jdk.tools", "jdk.tools")
    }
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.mockito_core)
}

description = "flink-hadoop-fs"

flinkCreateTestJar()
