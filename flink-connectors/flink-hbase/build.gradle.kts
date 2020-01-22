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
    api(Libs.flink_shaded_hadoop_2)

    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-streaming-java"))
    implementation(Libs.jsr305)
    implementation(Libs.hbase_server version stringProperty("hbase.version")) {
        // Remove unneeded dependency, which is conflicting with our jetty-util version.
        exclude(group = "org.mortbay.jetty", module = "jetty-util")
        exclude(group = "org.mortbay.jetty", module = "jetty")
        exclude(group = "org.mortbay.jetty", module = "jetty-sslengine")
        exclude(group = "org.mortbay.jetty", module = "jsp-2.1")
        exclude(group = "org.mortbay.jetty", module = "jsp-api-2.1")
        exclude(group = "org.mortbay.jetty", module = "servlet-api-2.5")
        // The hadoop dependencies are handled through flink-shaded-hadoop
        exclude(group = "org.apache.hadoop", module = "hadoop-common")
        exclude(group = "org.apache.hadoop", module = "hadoop-auth")
        exclude(group = "org.apache.hadoop", module = "hadoop-annotations")
        exclude(group = "org.apache.hadoop", module = "hadoop-mapreduce-client-core")
        exclude(group = "org.apache.hadoop", module = "hadoop-client")
        exclude(group = "org.apache.hadoop", module = "hadoop-hdfs")
        // Bug in hbase annotations, can be removed when fixed. See FLINK-2153.
        exclude(group = "org.apache.hbase", module = "hbase-annotations")
        exclude(group = "com.sun.jersey", module = "jersey-core")
        exclude(group = "com.sun.jersey", module = "jersey-server")
        exclude(group = "tomcat", module = "jasper-compiler")
        exclude(group = "tomcat", module = "jasper-runtime")
        exclude(group = "org.jruby.jcodings", module = "jcodings")
        exclude(group = "org.jruby.joni", module = "joni")
        exclude(group = "org.jamon", module = "jamon-runtime")
    }

    testImplementation(project(":flink-clients"))
    testImplementation(project(":flink-connectors:flink-hadoop-compatibility"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-scala"))
    testImplementation(project(":flink-table:flink-table-planner", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner-blink", configuration = TEST_JAR))
    testImplementation(Libs.hbase_server classifier "tests")
    testImplementation(Libs.hadoop_minicluster version stringProperty("hadoop.version"))
    testImplementation(Libs.hadoop_hdfs)

    flinkDependencyGroup(version = stringProperty("hbase.version")) {
        testImplementation(Libs.hbase_hadoop_compat)
        testImplementation(Libs.hbase_hadoop2_compat)
        testImplementation(Libs.hbase_server)
    }
}

description = "flink-hbase"
