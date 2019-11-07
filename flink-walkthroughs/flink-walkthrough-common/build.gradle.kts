dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
}

description = "flink-walkthrough-common"
