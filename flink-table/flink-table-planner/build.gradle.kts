plugins {
    scala
}

dependencies {
    api(Libs.scala_parser_combinators_2_11)

    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-api-scala-bridge"))
    implementation(project(":flink-table:flink-sql-parser"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-libraries:flink-cep"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.janino)
    implementation(Libs.calcite_core)
    implementation(Libs.joda_time)

    testImplementation(project(":flink-table:flink-table-common", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests", configuration = "testArtifacts"))
    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(project(":flink-streaming-java", configuration = "testArtifacts"))
}

description = "flink-table-planner"

flinkJointScalaJavaCompilation()

flinkCreateTestJar()
