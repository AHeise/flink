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

tasks.withType<ShadowJar> {
    // excluded all these files for a clean flink-table-planner jar
    exclude("org-apache-calcite-jdbc.properties")
    exclude("common.proto")
    exclude("requests.proto")
    exclude("responses.proto")
    exclude("codegen/**")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.RSA")
    exclude("META-INF/services/java.sql.Driver")

    //  Calcite is not relocated for now, because we expose it at some locations such as CalciteConfig
    // relocate("org.apache.calcite", "org.apache.flink.calcite.shaded.org.apache.calcite")

    //  Calcite's dependencies
    relocate("com.google", "org.apache.flink.calcite.shaded.com.google")
    relocate("com.jayway", "org.apache.flink.calcite.shaded.com.jayway")
    relocate("com.fasterxml", "org.apache.flink.calcite.shaded.com.fasterxml")
    relocate("org.apache.commons.codec", "org.apache.flink.calcite.shaded.org.apache.commons.codec")

    //  flink-table-planner dependencies
    relocate("org.joda", "org.apache.flink.table.shaded.org.joda")
    //  not relocated for now, because we need to change the contents of the properties field otherwise
    // relocate("org.codehaus", "org.apache.flink.table.shaded.org.codehaus")
}