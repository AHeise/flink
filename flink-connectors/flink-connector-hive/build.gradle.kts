dependencies {
    api(Libs.hive_metastore)
    api(project(":flink-core"))

    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-java"))
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-connectors:flink-hadoop-compatibility"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
//    implementation(Libs.flink_shaded_hadoop_2_uber)
    implementation(Libs.hive_exec)

    testImplementation(project(":flink-table:flink-table-common", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-api-java", configuration = "testArtifacts"))
    testImplementation(project(":flink-table:flink-table-planner-blink", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-clients"))
    testImplementation(project(":flink-formats:flink-csv"))
    testImplementation(Libs.hiverunner) {
        exclude(group = "javax.jms", module = "jms")
    }
    testImplementation(Libs.reflections)
    testImplementation(Libs.hive_service)
    testImplementation(Libs.hive_hcatalog_core)
    testImplementation(Libs.flink_shaded_guava)
}

description = "flink-connector-hive"

flinkExclude(group = "org.apache.calcite", name = "calcite-core")
flinkCreateTestJar()