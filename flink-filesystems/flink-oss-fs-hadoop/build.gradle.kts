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
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    flinkDependencyGroup(stringProperty("fs.hadoopshaded.version")) {
        implementation(Libs.hadoop_aliyun)
        implementation(Libs.aliyun_sdk_oss)
    }
    testImplementation(project(path = ":flink-filesystems:flink-fs-hadoop-shaded", configuration = TEST_JAR))
    testImplementation(project(path = ":flink-core", configuration = TEST_JAR))
    testImplementation(project(path = ":flink-filesystems:flink-hadoop-fs", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    implementation(project(":flink-core"))
}

tasks.withType<ShadowJar> {
    exclude(".gitkeep")
    exclude("mime.types")
    exclude("mozilla/**")
    exclude("META-INF/maven/**")


    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")
    //  relocate the OSS dependencies
    relocate("com.aliyun", "org.apache.flink.fs.osshadoop.shaded.com.aliyun")
    relocate("com.aliyuncs", "org.apache.flink.fs.osshadoop.shaded.com.aliyuncs")
}