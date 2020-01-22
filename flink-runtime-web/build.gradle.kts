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
    implementation(project(":flink-runtime"))
    implementation(project(":flink-clients"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.javassist)
    implementation(Libs.commons_io)
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.curator_test)
    testImplementation(Libs.flink_shaded_jackson_module_jsonschema)
    testImplementation(Libs.scala_library)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
}

description = "flink-runtime-web"

// TODO: gradle test
flinkCreateTestJar(mainClass = "org.apache.flink.runtime.webmonitor.handlers.utils.TestProgram",
        artifactName = "test-program") {
    include("org/apache/flink/runtime/webmonitor/handlers/utils/TestProgram.java")
}
flinkCreateTestJar(mainClass = "org.apache.flink.runtime.webmonitor.handlers.utils.TestProgram",
        artifactName = stringProperty("test.parameterProgram.name").toString()) {
    include("org/apache/flink/runtime/webmonitor/handlers/utils/TestProgram.java")
}
flinkCreateTestJar(artifactName = stringProperty("test.ParameterProgramNoManifest.name").toString()) {
    include("org/apache/flink/runtime/webmonitor/handlers/utils/TestProgram.java")
}