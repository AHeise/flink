dependencies {
    api(project(":flink-core"))
    api(project(":flink-table:flink-table-common"))
    api(Libs.flink_shaded_jackson)

    compileOnly(project(":flink-annotations"))

    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.hamcrest_all)
}

description = "flink-json"
