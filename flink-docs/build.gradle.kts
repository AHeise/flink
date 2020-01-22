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
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-runtime", configuration = TEST_JAR))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-metrics:flink-metrics-prometheus"))
    implementation(project(":flink-runtime-web"))
    implementation(project(":flink-yarn"))
    implementation(project(":flink-mesos"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_jackson_module_jsonschema)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.jsoup version "1.11.2")
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.junit)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-docs"

tasks.test {
    systemProperty("rootDir", project.rootDir)
}