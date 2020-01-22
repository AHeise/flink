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
    
    shade(project(":flink-filesystems:flink-s3-fs-base"))
//    shade(project(":flink-filesystems:flink-hadoop-fs"))
//    shade(Libs.hadoop_aws)
//    shade(Libs.hadoop_common)
//    flinkDependencyGroup(version = stringProperty("fs.s3.aws.version")) {
//        shade(Libs.aws_java_sdk_core)
//        shade(Libs.aws_java_sdk_s3)
//        shade(Libs.aws_java_sdk_kms)
//        shade(Libs.aws_java_sdk_dynamodb)
//    }

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-s3-fs-hadoop"

tasks.withType<ShadowJar> {
    //  relocate the references to Hadoop to match the shaded Hadoop config
    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")
    //  relocate the AWS dependencies
    relocate("com.amazon", "org.apache.flink.fs.s3base.shaded.com.amazon")
    //  relocated S3 hadoop dependencies
    relocate("javax.xml.bind", "org.apache.flink.fs.s3hadoop.shaded.javax.xml.bind")
    //  shade Flink's Hadoop FS utility classes
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.s3hadoop.common")
}