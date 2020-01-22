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
    id("scala")
}

dependencies {
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.akka_actor)
    implementation(Libs.akka_remote)
    implementation(Libs.akka_slf4j)
    implementation(Libs.mesos version stringProperty("mesos.version"))
    implementation(Libs.fenzo_core version "0.10.1")
    implementation(Libs.commons_cli)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.grizzled_slf4j)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(Libs.scalatest)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.akka_testkit)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-mesos"

flinkJointScalaJavaCompilation()

tasks.withType<ShadowJar> {
    relocate("com.google.protobuf", "org.apache.flink.mesos.shaded.com.google.protobuf")
    relocate("com.fasterxml.jackson", "org.apache.flink.mesos.shaded.com.fasterxml.jackson")
}