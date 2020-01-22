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
    api(project(":flink-connectors:flink-connector-elasticsearch-base"))
    api(Libs.elasticsearch_rest_high_level_client)

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.log4j_to_slf4j version "2.9.1")
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-elasticsearch-base", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(project(":flink-formats:flink-json"))
    testImplementation(Libs.transport)
    testImplementation(Libs.transport_netty4_client)
    testImplementation(Libs.log4j_core version "2.9.1")
}

description = "flink-connector-elasticsearch6"

flinkDependencyManagement {
    dependencyGroup(stringProperty("elasticsearch.version")) {
        dependency(Libs.elasticsearch)
        dependency(Libs.elasticsearch_rest_high_level_client)
        dependency(Libs.transport)
        dependency(Libs.transport_netty4_client)
    }
}