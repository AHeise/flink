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
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.curator_test)
    testImplementation(Libs.scala_library)
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-queryable-state-runtime"
