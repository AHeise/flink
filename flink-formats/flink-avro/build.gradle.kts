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
    id("com.commercehub.gradle.plugin.avro") version "0.15.1"
}

dependencies {
    api(Libs.avro)
    api(project(":flink-core"))

    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-table:flink-table-common"))
    implementation(Libs.joda_time)
    implementation(Libs.jsr305)
    implementation(Libs.kryo)

    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-api-java-bridge"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-scala", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
}

description = "flink-avro"

tasks.withType<com.commercehub.gradle.plugin.avro.GenerateAvroJavaTask>().named("generateTestAvroJava") {
    setSource(file("src/test/resources/avro"))
    stringType = "CharSequence"
    setEnableDecimalLogicalType("false")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("maven")
}