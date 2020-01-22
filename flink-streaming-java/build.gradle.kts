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

flinkCreateTestJar()

dependencies {
    api(project(":flink-core"))
    api(project(":flink-optimizer"))

    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.commons_math3)
    implementation(Libs.commons_io)
    implementation(Libs.commons_lang3)
    implementation(Libs.scala_library)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testApi(project(":flink-runtime", configuration = TEST_JAR))

    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.powermock_module_junit4)
}

description = "flink-streaming-java"