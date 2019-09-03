dependencies {
    api(project(":flink-core"))
    api(project(":flink-table:flink-table-common"))
    api(Libs.flink_shaded_jackson)

    compileOnly(project(":flink-annotations"))

    testImplementation(project(":flink-core"))
    testImplementation(project(path = ":flink-table:flink-table-common", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-csv"
