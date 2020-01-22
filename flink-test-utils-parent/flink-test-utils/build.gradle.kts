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

plugins {
    id("com.github.johnrengelman.shadow")
    id("com.github.jk1.dependency-license-report")
}

dependencies {
    api(project(":flink-test-utils-parent:flink-test-utils-junit"))
    api(project(":flink-clients"))

    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(":flink-runtime", configuration = TEST_JAR))
    implementation(project(":flink-streaming-java"))
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)

    shade(Libs.flink_shaded_netty)

    implementation(Libs.curator_test)
    implementation(Libs.hadoop_minikdc version stringProperty("minikdc.version"))
    implementation(Libs.scala_library)
}

description = "flink-test-utils"

licenseReport {
    renderers = arrayOf<com.github.jk1.license.render.ReportRenderer>(com.github.jk1.license.render.TextReportRenderer("report.txt"))
//    filters = arrayOf<com.github.jk1.license.filter.DependencyFilter>(com.github.jk1.license.filter.LicenseBundleNormalizer())
}

tasks.withType<ShadowJar> {
    dependencies {
        include(dependency("io.netty:netty"))
    }

    // TODO: gradle: check if this is actually helping at all; it's not a direct dependency, so the shaded stuff is only partially used
    relocate("org.jboss.netty", "org.apache.flink.shaded.testutils.org.jboss.netty")
    exclude("META-INF/maven/io.netty/**")
}