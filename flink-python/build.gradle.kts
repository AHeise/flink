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
    implementation(Libs.py4j version stringProperty("py4j.version"))
    implementation(Libs.pyrolite version "4.13") {
        exclude(group = "net.razorvine", module = "serpent")
    }
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_cli)
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-common"))
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.junit)
}

description = "flink-python"

tasks.withType<ShadowJar> {
    relocate("py4j", "org.apache.flink.api.python.shaded.py4j")
    relocate("net.razorvine", "org.apache.flink.api.python.shaded.net.razorvine")
}