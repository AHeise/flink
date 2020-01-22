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
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-dist"))
    testImplementation(project(":flink-clients", configuration = TEST_JAR))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-yarn", configuration = TEST_JAR))
    testImplementation(project(":flink-examples:flink-examples-batch"))
    testImplementation(project(":flink-examples:flink-examples-streaming"))
    testImplementation(Libs.hadoop_common)
    flinkDependencyGroup(version = stringProperty("hadoop.version")) {
        testImplementation(Libs.hadoop_yarn_client)
        testImplementation(Libs.hadoop_yarn_api)
        testImplementation(Libs.hadoop_minicluster)
        testImplementation(Libs.hadoop_minikdc)
    }
    testImplementation(Libs.flink_shaded_guava)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-yarn-tests"
