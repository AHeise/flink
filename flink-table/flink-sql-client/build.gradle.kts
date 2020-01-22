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
    implementation(project(":flink-clients"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(project(":flink-table:flink-table-planner-blink"))
    implementation(project(":flink-table:flink-table-runtime-blink"))
    implementation(project(":flink-connectors:flink-connector-hive"))
    implementation(Libs.hadoop_mapreduce_client_core)
    flinkDependencyGroup(version = "3.9.0") {
        implementation(Libs.jline_terminal)
        implementation(Libs.jline_reader)
    }
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_cli)

    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-clients", configuration = TEST_JAR))
    testImplementation(project(":flink-connectors:flink-connector-hive", configuration = TEST_JAR))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(Libs.hive_metastore)
    testImplementation(Libs.hive_exec)
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.mockito_core)
}

description = "flink-sql-client"

flinkExclude(group = "org.apache.calcite", name = "calcite-core")