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
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-formats:flink-avro"))
    implementation(project(":flink-formats:flink-json"))
    implementation(project(":flink-formats:flink-csv"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka-0.9"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka-0.10"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka-0.11"))
    implementation(project(":flink-connectors:flink-sql-connector-kafka"))
    implementation(project(":flink-connectors:flink-sql-connector-elasticsearch6"))
}

description = "flink-sql-client-test"

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("SqlToolbox")
}