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
    implementation(project(":flink-core"))

    shade(project(":flink-filesystems:flink-hadoop-fs"))
    shade(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    shade(Libs.hadoop_azure version stringProperty("fs.hadoopshaded.version"))

    testImplementation(Libs.azure version "1.16.0")
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(path = ":flink-core", configuration = TEST_JAR))
}

description = "flink-azure-fs-hadoop"

tasks.withType<ShadowJar> {
    exclude("properties.dtd")
    exclude("PropertyList-1.0.dtd")
    exclude("mozilla/**")
    exclude("META-INF/maven/**")
    exclude("META-INF/LICENSE.txt")

    //  relocate the references to Hadoop to match the shaded Hadoop config
    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")

    //  shade dependencies internally used by Hadoop and never exposed downstream
    relocate("org.apache.commons", "org.apache.flink.fs.shaded.hadoop3.org.apache.commons")

    //  relocate the Azure dependencies
    relocate("com.microsoft.azure", "org.apache.flink.fs.azure.shaded.com.microsoft.azure")

    //  shade dependencies internally used by Azure and never exposed downstream
    relocate("org.apache.httpcomponents", "org.apache.flink.fs.azure.shaded.org.apache.httpcomponents")
    relocate("commons-logging", "org.apache.flink.fs.azure.shaded.commons-logging")
    relocate("commons-codec", "org.apache.flink.fs.azure.shaded.commons-codec")
    relocate("com.fasterxml", "org.apache.flink.fs.azure.shaded.com.fasterxml")
    relocate("com.google", "org.apache.flink.fs.azure.shaded.com.google")
    relocate("org.eclipse", "org.apache.flink.fs.azure.shaded.org.eclipse")

    //  shade Flink's Hadoop FS adapter classes
    relocate("org.apache.flink.runtime.fs.hdfs", "org.apache.flink.fs.azure.common.hadoop")
    //  shade Flink's Hadoop FS utility classes
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.azure.common")
}