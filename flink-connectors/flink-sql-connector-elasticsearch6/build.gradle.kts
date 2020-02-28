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
    shade(project(":flink-connectors:flink-connector-elasticsearch6"))
}

description = "flink-sql-connector-elasticsearch6"

tasks.withType<ShadowJar> {
    dependencies {
        // These dependencies are not required.
        exclude(dependency("com.carrotsearch:hppc"))
        exclude(dependency("com.tdunning:t-digest"))
        exclude(dependency("joda-time:joda-time"))
        exclude(dependency("net.sf.jopt-simple:jopt-simple"))
        exclude(dependency("org.elasticsearch:jna"))
        exclude(dependency("org.hdrhistogram:HdrHistogram"))
        exclude(dependency("org.yaml:snakeyaml"))
    }
    // Unless otherwise noticed these filters only serve to reduce the size of the resulting  jar by removing unnecessary files
    // org.elasticsearch:elasticsearch
    exclude("config/**")
    exclude("modules.txt")
    exclude("plugins.txt")
    exclude("org/joda/**")
    // org.elasticsearch.client:elasticsearch-rest-high-level-client
    exclude("forbidden/**")
    // org.apache.httpcomponents:httpclient
    exclude("mozilla/**")
    // org.apache.lucene:lucene-analyzers-common
    exclude("org/tartarus/**")

    // Force relocation of all Elasticsearch dependencies.
    relocate("org.apache.commons", "org.apache.flink.elasticsearch6.shaded.org.apache.commons")
    relocate("org.apache.http", "org.apache.flink.elasticsearch6.shaded.org.apache.http")
    relocate("org.apache.lucene", "org.apache.flink.elasticsearch6.shaded.org.apache.lucene")
    relocate("org.elasticsearch", "org.apache.flink.elasticsearch6.shaded.org.elasticsearch")
    relocate("org.apache.logging", "org.apache.flink.elasticsearch6.shaded.org.apache.logging")
    relocate("com.fasterxml.jackson", "org.apache.flink.elasticsearch6.shaded.com.fasterxml.jackson")
}