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
    implementation(Libs.slf4j_api)
    implementation(Libs.jsr305)

    shade(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    shade(project(":flink-filesystems:flink-hadoop-fs"))

    flinkDependencyGroup(version = stringProperty("fs.s3.aws.version")) {
        shade(Libs.aws_java_sdk_core)
        shade(Libs.aws_java_sdk_s3)
        shade(Libs.aws_java_sdk_kms)
        shade(Libs.aws_java_sdk_dynamodb)
    }
    shade(Libs.hadoop_aws version stringProperty("fs.hadoopshaded.version")) {
        exclude("com.amazonaws","aws-java-sdk-bundle")
    }
//    shade(Libs.commons_io)

    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.slf4j_log4j12)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-s3-fs-base"

tasks.withType<ShadowJar> {
    exclude(".gitkeep")
    exclude("mime.types")
    exclude("mozilla/**")
    exclude("META-INF/maven/**")
    exclude("META-INF/LICENSE.txt")

    exclude("org/apache/flink/runtime/util/HadoopUtils")
    exclude("org/apache/flink/runtime/fs/hdfs/HadoopRecoverable*")

    //  shade dependencies internally used by Hadoop and never exposed downstream
    relocate("org.apache.commons", "org.apache.flink.fs.shaded.hadoop3.org.apache.commons")

    //  shade dependencies internally used by AWS and never exposed downstream
    relocate("software.amazon", "org.apache.flink.fs.s3base.shaded.software.amazon")
    relocate("org.joda", "org.apache.flink.fs.s3base.shaded.org.joda")
    relocate("org.apache.http", "org.apache.flink.fs.s3base.shaded.org.apache.http")
    relocate("com.fasterxml", "org.apache.flink.fs.s3base.shaded.com.fasterxml")
    relocate("com.google", "org.apache.flink.fs.s3base.shaded.com.google")

    //  shade Flink's Hadoop FS adapter classes
    relocate("org.apache.flink.runtime.fs.hdfs", "org.apache.flink.fs.s3.common.hadoop")
}