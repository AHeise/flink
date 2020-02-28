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
    shade(project(":flink-connectors:flink-connector-elasticsearch-base"))

    shade(Libs.transport)
    shade(Libs.transport_netty4_client)
    shade(Libs.log4j_to_slf4j version "2.7")
    implementation(Libs.jsr305)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-elasticsearch-base", configuration = TEST_JAR))
    testImplementation(Libs.log4j_core version "2.7")
    configurations["testRuntime"].exclude(Libs.log4j_to_slf4j)
}

description = "flink-connector-elasticsearch5"

flinkDependencyManagement {
    dependencyGroup(stringProperty("elasticsearch.version")) {
        dependency(Libs.elasticsearch)
        dependency(Libs.transport)
        dependency(Libs.transport_netty4_client)
    }
}

tasks.withType<ShadowJar> {
    // *
    exclude("log4j.properties")
    exclude("config/favicon.ico")
    exclude("mozilla/**")
    exclude("META-INF/maven/com*/**")
    exclude("META-INF/maven/io*/**")
    exclude("META-INF/maven/joda*/**")
    exclude("META-INF/maven/net*/**")
    exclude("META-INF/maven/org.an*/**")
    exclude("META-INF/maven/org.apache.h*/**")
    exclude("META-INF/maven/org.apache.commons/**")
    exclude("META-INF/maven/org.apache.flink/force-shading/**")
    exclude("META-INF/maven/org.apache.logging*/**")
    exclude("META-INF/maven/org.e*/**")
    exclude("META-INF/maven/org.h*/**")
    exclude("META-INF/maven/org.j*/**")
    exclude("META-INF/maven/org.y*/**")
    //  some dependencies bring their own LICENSE.txt which we don't need
    // *:*
    exclude("META-INF/LICENSE.txt")
    // io.netty:netty
    // Only some of these licenses actually apply to the JAR and have been manually
    // placed in this module's resources directory.
    exclude("META-INF/license")
    // Only parts of NOTICE file actually apply to the netty JAR and have been manually
    // copied into this modules's NOTICE file.
    exclude("META-INF/NOTICE.txt")

    relocate("com.carrotsearch", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.carrotsearch")
    relocate("com.fasterxml", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.fasterxml")
    relocate("com.github", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.github")
    relocate("com.sun", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.sun")
    relocate("com.tdunning", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.tdunning")
    relocate("io.netty", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.io.netty")
    relocate("org.apache", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.apache") {
        //  keep flink classes as they are (exceptions as above)
        exclude("org.apache.flink.**")
        exclude("org.apache.log4j.**")
    }
    relocate("org.HdrHistogram", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.HdrHistogram")
    relocate("org.jboss", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.jboss")
    relocate("org.joda", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.joda")
    relocate("org.tartarus", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.tartarus")
    relocate("org.yaml", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.yaml")
    relocate("joptsimple", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.joptsimple")
}