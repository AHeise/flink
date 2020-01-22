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
    implementation(Libs.flink_shaded_guava)
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
}

description = "flink-queryable-state-test"

flinkSetMainClass("org.apache.flink.streaming.tests.queryablestate.QsStateProducer")

val testJar by tasks.register<ShadowJar>("QsStateClient") {
    configurations = listOf(project.configurations["runtimeClasspath"])

    include("org.apache.flink:flink-queryable-state-test*")

    archiveClassifier.set("QsStateClient")

    manifest {
        attributes(mapOf("Main-Class" to "org.apache.flink.streaming.tests.queryablestate.QsStateClient"))
    }
}