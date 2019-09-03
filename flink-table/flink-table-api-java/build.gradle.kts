dependencies {
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)
    api(project(":flink-table:flink-table-common"))
    implementation(project(":flink-annotations"))
    implementation(project(":flink-core"))
    testImplementation(project(path = ":flink-table:flink-table-common", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-table-api-java"

flinkCreateTestJar()