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
    api(Libs.kafka_clients version stringProperty("kafka.version"))

    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_collections)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-metrics:flink-metrics-jmx"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests", configuration = TEST_JAR))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(Libs.hadoop_minikdc version stringProperty("minikdc.version"))
    testImplementation(Libs.kafka version stringProperty("kafka.version"))
    testImplementation(Libs.zkclient version "0.7")
    testImplementation(Libs.curator_test)
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-connector-kafka-base"

flinkCreateTestJar()