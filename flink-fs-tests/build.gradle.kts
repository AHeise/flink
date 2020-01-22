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
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-examples:flink-examples-batch"))
    testImplementation(project(":flink-formats:flink-avro"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-runtime", configuration = TEST_JAR))
    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(Libs.hadoop_hdfs classifier "tests" version stringProperty("hadoop.version"))
    testImplementation(Libs.hadoop_common)
    testImplementation(Libs.flink_shaded_hadoop_2)
    testImplementation(Libs.mockito_core)
}

description = "flink-fs-tests"
