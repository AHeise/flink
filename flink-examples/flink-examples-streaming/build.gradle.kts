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
    api(project(":flink-streaming-scala"))
    api(project(":flink-streaming-java"))

    implementation(project(":flink-connectors:flink-connector-twitter"))
    implementation(project(":flink-connectors:flink-connector-kafka"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_io)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
}

description = "flink-examples-streaming"