dependencies {
    api(project(":flink-table:flink-table-common"))

    compileOnly(project(":flink-annotations"))

    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)
    implementation(project(":flink-core"))

    testImplementation(project(path = ":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-table-api-java"

flinkCreateTestJar()