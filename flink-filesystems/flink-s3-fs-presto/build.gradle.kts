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
    implementation(Libs.jsr305)

    shade(project(":flink-filesystems:flink-s3-fs-base"))
    shade(Libs.presto_hive version stringProperty("presto.version")) {
        // lot's of unneeded stuff for the S3 file system
        exclude(group = "com.facebook.hive", module = "hive-dwrf")
        exclude(group = "com.facebook.presto.hive", module = "hive-apache")
        exclude(group = "com.facebook.presto", module = "presto-spi")
        exclude(group = "com.facebook.presto", module = "presto-plugin-toolkit")
        exclude(group = "com.facebook.presto", module = "presto-orc")
        exclude(group = "com.facebook.presto", module = "presto-rcfile")
        exclude(group = "org.slf4j", module = "slf4j-jdk14")
        exclude(group = "org.slf4j", module = "log4j-over-slf4j")
        exclude(group = "org.slf4j", module = "jcl-over-slf4j")
        exclude(group = "org.apache.thrift", module = "libthrift")
        exclude(group = "io.airlift", module = "json")
        exclude(group = "io.airlift", module = "bootstrap")
        exclude(group = "io.airlift", module = "concurrent")
        exclude(group = "io.airlift", module = "event")
        exclude(group = "io.airlift", module = "http-client")
        exclude(group = "io.airlift", module = "aircompressor")
        exclude(group = "io.airlift", module = "log-manager")
        exclude(group = "javax.inject", module = "javax.inject")
        exclude(group = "com.google.inject", module = "guice")
        exclude(group = "com.google.inject.extensions", module = "guice-multibindings")
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "org.xerial.snappy", module = "snappy-java")
        exclude(group = "org.apache.bval", module = "bval-jsr")
        exclude(group = "javax.validation", module = "validation-api")
        exclude(group = "org.openjdk.jol", module = "jol-core")
        exclude(group = "cglib", module = "cglib-nodep")
        exclude(group = "com.google.code.findbugs", module = "annotations")
    }
    shade(Libs.hadoop_apache2 version "2.7.3-1")

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-s3-fs-presto"

tasks.withType<ShadowJar> {
    exclude("META-INF/maven/org.weakref/**")
    exclude("META-INF/maven/org.hdrhistogram/**")
    exclude("META-INF/maven/joda-time/**")
    exclude("META-INF/maven/io.airlift/**")
    exclude("META-INF/maven/com*/**")
    exclude("META-INF/maven/org.apache.flink/force-shading/**")
    exclude("META-INF/LICENSE.txt")
    exclude("org/apache/hadoop/**")

    //  relocate the references to Hadoop to match the pre-shaded hadoop artifact
    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")
    //  relocate the AWS dependencies
    relocate("com.amazon", "org.apache.flink.fs.s3base.shaded.com.amazon")
    //  relocate S3 presto and dependencies
    relocate("com.facebook", "org.apache.flink.fs.s3presto.shaded.com.facebook")
    relocate("com.fasterxml", "org.apache.flink.fs.s3presto.shaded.com.fasterxml")
    relocate("io.airlift", "org.apache.flink.fs.s3presto.shaded.io.airlift")
    relocate("javax.xml.bind", "org.apache.flink.fs.s3presto.shaded.javax.xml.bind")
    relocate("org.HdrHistogram", "org.apache.flink.fs.s3presto.shaded.org.HdrHistogram")
    relocate("org.joda", "org.apache.flink.fs.s3presto.shaded.org.joda")
    relocate("org.weakref", "org.apache.flink.fs.s3presto.shaded.org.weakref")
    relocate("org.openjdk", "org.apache.flink.fs.s3presto.shaded.org.openjdk")
    relocate("com.google", "org.apache.flink.fs.s3presto.shaded.com.google")

    //  shade Flink's Hadoop FS utility classes
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.s3presto.common")
}