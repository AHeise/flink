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

dependencies {
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-libraries:flink-cep"))
    implementation(Libs.janino version stringProperty("janino.version"))
    // When updating the Calcite version, make sure to update the version and dependency exclusions
    implementation(Libs.avatica_core version "1.15.0") {
        /*
        Dependencies that are not needed for how we use Avatica right now.

        We exclude all the dependencies of Avatica because currently we only use
                TimeUnit, TimeUnitRange and SqlDateTimeUtils which only dependent JDK.

        "mvn dependency:tree" as of Avatica 1.15:

        [INFO] +- org.apache.calcite.avatica:avatica-core:jar:1.15.0:compile
        */
        exclude(group = "org.apache.calcite.avatica", module = "avatica-metrics")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "org.apache.httpcomponents", module = "httpclient")
        exclude(group = "org.apache.httpcomponents", module = "httpcore")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-annotations")
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
    }
    implementation(Libs.lz4_java version "1.5.0")
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_math3)
    implementation(Libs.scala_library)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-table-runtime-blink"

flinkCreateTestJar()

tasks.withType<ShadowJar> {
    //  Relocate org.lz4:lz4-java
    relocate("net.jpountz", "org.apache.flink.table.shaded.net.jpountz")
}