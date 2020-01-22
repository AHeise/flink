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

repositories {
    maven(url = "https://packages.confluent.io/maven/")
}

dependencies {
    api(project(":flink-formats:flink-avro"))
    api(Libs.kafka_schema_registry_client version "3.3.1")

    implementation(Libs.jsr305)

    testImplementation(Libs.junit)
}

tasks.withType<ShadowJar> {
    relocate("com.fasterxml.jackson", "org.apache.flink.formats.avro.registry.confluent.shaded.com.fasterxml.jackson")
    relocate("org.apache.zookeeper", "org.apache.flink.formats.avro.registry.confluent.shaded.org.apache.zookeeper")
    relocate("org.apache.jute", "org.apache.flink.formats.avro.registry.confluent.shaded.org.apache.jute")
    relocate("org.I0Itec.zkclient", "org.apache.flink.formats.avro.registry.confluent.shaded.org.101tec")
}