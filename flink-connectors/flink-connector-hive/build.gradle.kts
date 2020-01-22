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
    flinkDependencyGroup(version = stringProperty("hive.version")) {
        api(Libs.hive_metastore)
        implementation(Libs.hive_exec) {
            exclude(group = "org.apache.hive", module = "hive-vector-code-gen")
            exclude(group = "org.apache.hive", module = "hive-llap-tez")
            exclude(group = "org.apache.hive", module = "hive-shims")
            exclude(group = "commons-codec", module = "commons-codec")
            exclude(group = "commons-httpclient", module = "commons-httpclient")
            exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j-impl")
            exclude(group = "org.antlr", module = "antlr-runtime")
            exclude(group = "org.antlr", module = "ST4")
            exclude(group = "org.apache.ant", module = "ant")
            exclude(group = "org.apache.commons", module = "commons-compress")
            exclude(group = "org.apache.ivy", module = "ivy")
            exclude(group = "org.apache.zookeeper", module = "zookeeper")
            exclude(group = "org.apache.curator", module = "apache-curator")
            exclude(group = "org.apache.curator", module = "curator-framework")
            exclude(group = "org.codehaus.groovy", module = "groovy-all")
            exclude(group = "org.apache.calcite", module = "calcite-core")
            exclude(group = "org.apache.calcite", module = "calcite-druid")
            exclude(group = "org.apache.calcite.avatica", module = "avatica")
            exclude(group = "org.apache.calcite", module = "calcite-avatica")
            exclude(group = "com.google.code.gson", module = "gson")
            exclude(group = "stax", module = "stax-api")
            exclude(group = "com.google.guava", module = "guava")
        }
    }
    api(project(":flink-core"))

    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-java"))
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-connectors:flink-hadoop-compatibility"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
//    implementation(Libs.flink_shaded_hadoop_2_uber)

    testImplementation(project(":flink-table:flink-table-common", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-api-java", configuration = TEST_JAR))
    testImplementation(project(":flink-table:flink-table-planner-blink", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-table:flink-table-planner"))
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-clients"))
    testImplementation(project(":flink-formats:flink-csv"))
    testImplementation(Libs.hiverunner version stringProperty("hiverunner.version")) {
        exclude(group = "javax.jms", module = "jms")
    }
    testImplementation(Libs.reflections)
    flinkDependencyGroup(version = stringProperty("hive.version")) {
        testImplementation(Libs.hive_service)
        testImplementation(Libs.hive_hcatalog_core)
    }
    testImplementation(Libs.flink_shaded_guava)
}

description = "flink-connector-hive"

flinkDependencyManagement {
    dependency(Libs.reflections, version = "0.9.8")
}

flinkExclude(group = "org.apache.calcite", name = "calcite-core")
flinkCreateTestJar()