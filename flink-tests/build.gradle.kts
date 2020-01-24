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
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.scala_library)

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-optimizer", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime-web"))
    testImplementation(project(":flink-clients"))
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-scala"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-examples:flink-examples-batch"))
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(project(":flink-optimizer"))
    testImplementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(Libs.flink_shaded_hadoop_2)
    testImplementation(Libs.flink_shaded_jackson)
    testImplementation(Libs.flink_shaded_netty)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.scalatest)
    testImplementation(Libs.akka_testkit)
    testImplementation(Libs.joda_time)
    testImplementation(Libs.joda_convert)
    testImplementation(Libs.oshi_core)
    testImplementation(Libs.reflections)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-tests"

flinkCreateTestJar()

// TODO: gradle create jars for the different test programs