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
    api(Libs.frocksdbjni version "5.17.2-artisans-1.0")

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_io)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-statebackend-rocksdb"
