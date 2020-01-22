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
    implementation(project(":flink-java"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-libraries:flink-gelly"))
    implementation(project(":flink-libraries:flink-gelly-scala"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_math3)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-optimizer"))
}

description = "flink-gelly-examples"

flinkSetMainClass("org.apache.flink.graph.Runner")