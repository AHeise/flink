plugins {
    scala
}

dependencies {
    api(Libs.scala_parser_combinators)

    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-api-scala-bridge"))
    implementation(project(":flink-table:flink-sql-parser")) {
        exclude(group = "org.apache.calcite", module = "calcite-core")
    }
    implementation(project(":flink-scala"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-libraries:flink-cep"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.janino)
    implementation(Libs.jsr305)
    // When updating the Calcite version, make sure to update the dependency exclusions
    implementation(Libs.calcite_core version "1.20.0") {
        /*
        "mvn dependency:tree" as of Calcite 1.20:

        [INFO] +- org.apache.calcite:calcite-core:jar:1.20.0:compile
        [INFO] |  +- commons-codec:commons-codec:jar:1.10:compile
        [INFO] |  +- org.apache.calcite:calcite-linq4j:jar:1.20.0:compile
        [INFO] |  +- org.apache.commons:commons-lang3:jar:3.3.2:compile
        [INFO] |  +- com.fasterxml.jackson.core:jackson-core:jar:2.9.6:compile
        [INFO] |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.9.6:compile
        [INFO] |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.9.6:compile
        [INFO] |  +- com.google.guava:guava:jar:19.0:compile
        [INFO] |  \- com.jayway.jsonpath:json-path:jar:2.4.0:compile

        Dependencies that are not needed for how we use Calcite right now.
        */
        exclude(group = "org.apache.calcite.avatica", module = "avatica-metrics")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "org.apache.httpcomponents", module = "httpclient")
        exclude(group = "org.apache.httpcomponents", module = "httpcore")
        exclude(group = "org.apache.commons", module = "commons-dbcp2")
        exclude(group = "com.esri.geometry", module = "esri-geometry-api")
        exclude(group = "com.fasterxml.jackson.dataformat", module = "jackson-dataformat-yaml")
        exclude(group = "com.yahoo.datasketches", module = "sketches-core")
        exclude(group = "net.hydromatic", module = "aggdesigner-algorithm")
    }
    implementation(Libs.joda_time)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests", configuration = TEST_JAR))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

flinkDependencyManagement {

    // Common dependency of calcite-core and flink-test-utils -->
    dependency(group = "com.google.guava", name = "guava", version = "19.0")
    dependencyGroup(version = stringProperty("janino.version")) {
        // Common dependency of calcite-core and janino -->
        dependency(group = "org.codehaus.janino", name = "commons-compiler")
        // Common dependency of calcite-core and flink-table-planner -->
        dependency(group = "org.codehaus.janino", name = "janino")
    }
    // Common dependencies within calcite-core -->
    dependencyGroup(version = "2.9.6") {
        dependency(group = "com.fasterxml.jackson.core", name = "jackson-annotations")
        dependency(group = "com.fasterxml.jackson.core", name = "jackson-core")
        dependency(group = "com.fasterxml.jackson.core", name = "jackson-databind")
    }
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