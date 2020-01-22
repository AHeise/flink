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
    api(project(":flink-annotations"))

    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(Libs.commons_lang3)
    implementation(Libs.kryo)
    implementation(Libs.commons_collections)
    implementation(Libs.commons_compress)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_asm_7)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.commons_io)
    testImplementation(Libs.joda_time)
    testImplementation(Libs.joda_convert)
    testImplementation(Libs.flink_shaded_jackson)
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.hamcrest_all)

    flinkDependencyGroup(version = "1.18.10") {
        testImplementation(Libs.lombok)
        testAnnotationProcessor(Libs.lombok)
    }
}

description = "flink-core"

flinkCreateTestJar()
