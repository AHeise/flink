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
    shade(Libs.hadoop_common) {
        exclude("jdk.tools", "jdk.tools")
        exclude("com.jcraft", "jsch")
        exclude("com.sun.jersey", "jersey-core")
        exclude("com.sun.jersey", "jersey-servlet")
        exclude("com.sun.jersey", "jersey-json")
        exclude("com.sun.jersey", "jersey-server")
        exclude("org.apache.avro", "avro")
        exclude("org.slf4j", "slf4j-log4j12")
        exclude("log4j", "log4j")
        exclude("org.eclipse.jetty", "jetty-server")
        exclude("org.eclipse.jetty", "jetty-util")
        exclude("org.eclipse.jetty", "jetty-servlet")
        exclude("org.eclipse.jetty", "jetty-webapp")
        exclude("javax.servlet", "javax.servlet-api")
        exclude("javax.servlet.jsp", "jsp-api")
        exclude("org.apache.kerby", "kerb-simplekdc")
        exclude("org.apache.curator", "curator-client")
        exclude("org.apache.curator", "curator-framework")
        exclude("org.apache.curator", "curator-recipes")
        exclude("org.apache.zookeeper", "zookeeper")
        exclude("commons-net", "commons-net")
        exclude("commons-cli", "commons-cli")
        exclude("commons-codec", "commons-codec")
        exclude("com.google.protobuf", "protobuf-java")
        exclude("com.google.code.gson", "gson")
        exclude("org.apache.httpcomponents", "httpclient")
        exclude("org.apache.commons", "commons-compress")
        exclude("org.apache.commons", "commons-math3")
        exclude("com.nimbusds", "nimbus-jose-jwt")
        exclude("net.minidev", "json-smart")
    }
}

flinkDependencyManagement {
    dependency(Libs.hadoop_common, version = stringProperty("fs.hadoopshaded.version"))
}

description = "flink-filesystems :: flink-fs-hadoop-shaded"

flinkCreateTestJar()

tasks.withType<ShadowJar> {
    // org.apache.hadoop:hadoop-common
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("org/apache/hadoop/conf/ConfigurationWithLogging.class")
    exclude("core-default.xml")
    exclude("common-version-info.properties")
    exclude("org.apache.hadoop.application-classloader.properties")
    // *
    exclude("properties.dtd")
    exclude("PropertyList-1.0.dtd")
    exclude("META-INF/maven/**")
    exclude("META-INF/services/javax.xml.stream.*")
    exclude("META-INF/LICENSE.txt")

    // we shade only the parts that are internal to Hadoop and not used / exposed downstream
    relocate("com.google.re2j", "org.apache.flink.fs.shaded.hadoop3.com.google.re2j")
    relocate("org.apache.htrace", "org.apache.flink.fs.shaded.hadoop3.org.apache.htrace")
    relocate("com.fasterxml", "org.apache.flink.fs.shaded.hadoop3.com.fasterxml")
    relocate("org.codehaus", "org.apache.flink.fs.shaded.hadoop3.org.codehaus")
    relocate("com.ctc", "org.apache.flink.fs.shaded.hadoop3.com.ctc")
}