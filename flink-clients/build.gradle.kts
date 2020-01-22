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
    implementation(project(":flink-core"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(":flink-java"))
    implementation(Libs.commons_cli)
    implementation(Libs.scala_library) // we need this because ClusterClient uses FiniteDuration
    implementation(Libs.flink_shaded_netty) // we need this because RestClusterClient uses netty classes
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-clients"

flinkCreateTestJar("org.apache.flink.client.testjar.WordCount")

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("maven")
}