/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    scala
}

dependencies {
    api(Libs.scala_parser_combinators)

    compileOnly(Libs.reflections version "0.9.10") {
        exclude(group = "com.google.code.findbugs", module = "annotations")
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-table:flink-table-api-scala"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-api-scala-bridge"))
    implementation(project(":flink-table:flink-sql-parser")) {
        exclude(group = "org.apache.calcite", module = "calcite-core")
    }
    implementation(project(":flink-table:flink-table-runtime-blink"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-libraries:flink-cep"))
    implementation(Libs.janino)
    implementation(Libs.calcite_core version "1.20.0") {
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
    implementation(Libs.scala_library)
    implementation(Libs.commons_math3)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-table:flink-table-runtime-blink", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(project(":flink-table:flink-sql-parser"))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
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

description = "flink-table-planner-blink"

tasks.withType<ScalaCompile>().configureEach {
    scalaCompileOptions.additionalParameters = listOf("-nobootcp")
    scalaCompileOptions.forkOptions.apply {
        memoryMaximumSize = "1g"
        memoryInitialSize = "128m"
    }
}

flinkJointScalaJavaCompilation()
flinkCreateTestJar()

tasks.withType<ShadowJar> {
    exclude("org-apache-calcite-jdbc.properties")
    exclude("common.proto")
    exclude("requests.proto")
    exclude("responses.proto")
    exclude("codegen/**")
    exclude("META-INF/services/java.sql.Driver")


    //  Calcite is not relocated for now, because we expose it at some locations such as CalciteConfig
    // relocate("org.apache.calcite", "org.apache.flink.calcite.shaded.org.apache.calcite")

    //  Calcite's dependencies
    relocate("com.google", "org.apache.flink.calcite.shaded.com.google")
    relocate("com.jayway", "org.apache.flink.calcite.shaded.com.jayway")
    relocate("com.fasterxml", "org.apache.flink.calcite.shaded.com.fasterxml")
    relocate("org.apache.commons.codec", "org.apache.flink.calcite.shaded.org.apache.commons.codec")

    //  flink-table-planner dependencies
    //  not relocated for now, because we need to change the contents of the properties field otherwise
    // relocate("org.codehaus", "org.apache.flink.table.shaded.org.codehaus")
}