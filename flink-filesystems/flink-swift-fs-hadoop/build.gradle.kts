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
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    shade(project(":flink-filesystems:flink-hadoop-fs")) {
        exclude("org.apache.flink", "flink-shaded-hadoop-2")
    }
    flinkDependencyGroup(version = stringProperty("openstackhadoop.hadoop.version")) {
        shade(Libs.hadoop_client) {
            exclude(group = "org.apache.hadoop", module = "hadoop-mapreduce-client-core")
            exclude(group = "org.apache.hadoop", module = "hadoop-yarn-api")
            exclude(group = "org.apache.hadoop", module = "hadoop-mapreduce-client-jobclient")
            exclude(group = "org.apache.hadoop", module = "hadoop-mapreduce-client-app")
            exclude(group = "org.apache.avro", module = "avro")
            exclude(group = "javax.servlet.jsp", module = "jsp-api")
            exclude(group = "org.apache.directory.server", module = "apacheds-kerberos-codec")
            exclude(group = "org.apache.curator", module = "curator-client")
            exclude(group = "org.apache.curator", module = "curator-framework")
            exclude(group = "org.apache.curator", module = "curator-recipes")
            exclude(group = "org.apache.zookeeper", module = "zookeeper")
        }
        shade(Libs.hadoop_openstack) {
            exclude(group = "org.apache.hadoop", module = "hadoop-common")
        }
    }

    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-swift-fs-hadoop"

flinkDependencyManagement {
    dependency(Libs.hadoop_common, version = stringProperty("openstackhadoop.hadoop.version"))
}

tasks.withType<ShadowJar> {
    exclude("log4j.properties")
    exclude("mime.types")
    exclude("properties.dtd")
    exclude("PropertyList-1.0.dtd")
    exclude("models/**")
    exclude("mozilla/**")
    exclude("META-INF/maven/com*/**")
    exclude("META-INF/maven/net*/**")
    exclude("META-INF/maven/software*/**")
    exclude("META-INF/maven/joda*/**")
    exclude("META-INF/maven/org.mortbay.jetty/**")
    exclude("META-INF/maven/org.apache.h*/**")
    exclude("META-INF/maven/org.apache.commons/**")
    exclude("META-INF/maven/org.apache.flink/flink-hadoop-fs/**")
    exclude("META-INF/maven/org.apache.flink/force-shading/**")
    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/ASL2.0")
    exclude("META-INF/README.txt")
    // -we use our own "shaded" core -default.xml: core-default-shaded.xml
    exclude("core-default.xml")
    // we only add a core - site.xml with unshaded classnames for the unit tests-- >
    exclude("core-site.xml")

    relocate("com.fasterxml", "org.apache.flink.fs.openstackhadoop.shaded.com.fasterxml")
    relocate("com.google", "org.apache.flink.fs.openstackhadoop.shaded.com.google") {
        exclude("com.google.code.findbugs.**")
    }
    relocate("com.nimbusds", "org.apache.flink.fs.openstackhadoop.shaded.com.nimbusds")
    relocate("com.squareup", "org.apache.flink.fs.openstackhadoop.shaded.com.squareup")
    relocate("net.jcip", "org.apache.flink.fs.openstackhadoop.shaded.net.jcip")
    relocate("net.minidev", "org.apache.flink.fs.openstackhadoop.shaded.net.minidev")

    //  relocate everything from the flink-hadoop-fs project
    relocate("org.apache.flink.runtime.fs.hdfs", "org.apache.flink.fs.openstackhadoop.shaded.org.apache.flink.runtime.fs.hdfs")
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.openstackhadoop.shaded.org.apache.flink.runtime.util") {
        include("org.apache.flink.runtime.util.**Hadoop*")
    }

    relocate("org.apache", "org.apache.flink.fs.openstackhadoop.shaded.org.apache") {
        //  keep all other classes of flink as they are (exceptions above)
        exclude("org.apache.flink.**")
        exclude("org.apache.log4j.**") // provided
    }

    relocate("org.codehaus", "org.apache.flink.fs.openstackhadoop.shaded.org.codehaus")
    relocate("org.joda", "org.apache.flink.fs.openstackhadoop.shaded.org.joda")
    relocate("org.mortbay", "org.apache.flink.fs.openstackhadoop.shaded.org.mortbay")
    relocate("org.tukaani", "org.apache.flink.fs.openstackhadoop.shaded.org.tukaani")
    relocate("org.znerd", "org.apache.flink.fs.openstackhadoop.shaded.org.znerd")
    relocate("okio", "org.apache.flink.fs.openstackhadoop.shaded.okio")
}