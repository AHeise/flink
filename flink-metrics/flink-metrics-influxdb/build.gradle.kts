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
    api(project(":flink-core"))

    implementation(project(":flink-runtime"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.influxdb_java version "2.14")
    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.wiremock version "2.19.0")
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-metrics-influxdb"

tasks.withType<ShadowJar> {
    relocate("org.influxdb", "org.apache.flink.metrics.influxdb.shaded.org.influxdb")
    relocate("com.squareup.moshi", "org.apache.flink.metrics.influxdb.shaded.com.squareup.moshi")
    relocate("okhttp3", "org.apache.flink.metrics.influxdb.shaded.okhttp3")
    relocate("okio", "org.apache.flink.metrics.influxdb.shaded.okio")
    relocate("retrofit2", "org.apache.flink.metrics.influxdb.shaded.retrofit2")
}