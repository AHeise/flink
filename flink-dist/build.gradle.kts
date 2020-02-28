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

configurations {
    configurations.register("example") {
        isTransitive = false
    }
    configurations.register("opt") {
        isTransitive = false
    }
}

fun DependencyHandler.`example`(dependencyNotation: Any): Dependency? = add("example", dependencyNotation)

fun DependencyHandler.`opt`(dependencyNotation: Any): Dependency? = add("opt", dependencyNotation)

dependencies {
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-runtime-web"))
    implementation(project(":flink-optimizer"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-metrics:flink-metrics-core"))
    implementation(project(":flink-metrics:flink-metrics-jmx"))
    implementation(project(":flink-mesos"))
    implementation(project(":flink-container"))
    implementation(project(":flink-state-backends:flink-statebackend-rocksdb"))
    implementation(project(":flink-yarn"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(project(":flink-filesystems:flink-mapr-fs"))
    implementation(project(":flink-scala-shell"))
    
    example(project(":flink-examples:flink-examples-batch"))
    example(project(":flink-examples:flink-examples-streaming"))
    example(project(":flink-examples:flink-examples-build-helper:flink-examples-streaming-state-machine"))
    example(project(":flink-examples:flink-examples-build-helper:flink-examples-streaming-click-event-count"))
    example(project(":flink-examples:flink-examples-build-helper:flink-examples-streaming-twitter"))
    example(project(":flink-libraries:flink-gelly-examples"))

    opt(project(":flink-metrics:flink-metrics-dropwizard"))
    opt(project(":flink-metrics:flink-metrics-graphite"))
    opt(project(":flink-metrics:flink-metrics-influxdb"))
    opt(project(":flink-metrics:flink-metrics-prometheus"))
    opt(project(":flink-metrics:flink-metrics-statsd"))
    opt(project(":flink-metrics:flink-metrics-datadog"))
    opt(project(":flink-metrics:flink-metrics-slf4j"))
    opt(project(":flink-libraries:flink-cep"))
    opt(project(":flink-libraries:flink-cep-scala"))
    opt(project(":flink-libraries:flink-gelly"))
    opt(project(":flink-libraries:flink-gelly-scala"))
    opt(project(":flink-table:flink-table-uber"))
    opt(project(":flink-table:flink-table-uber-blink"))
    opt(project(":flink-table:flink-sql-client"))
    opt(project(":flink-libraries:flink-state-processor-api"))
    opt(project(":flink-filesystems:flink-azure-fs-hadoop"))
    opt(project(":flink-filesystems:flink-s3-fs-hadoop"))
    opt(project(":flink-filesystems:flink-s3-fs-presto"))
    opt(project(":flink-filesystems:flink-swift-fs-hadoop"))
    opt(project(":flink-filesystems:flink-oss-fs-hadoop"))
    opt(project(":flink-queryable-state:flink-queryable-state-runtime"))
    opt(project(":flink-python"))
    opt(Libs.flink_shaded_netty_tcnative_dynamic)

    testImplementation(Libs.hamcrest_all)
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-dist"
