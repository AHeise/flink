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
    api(project(":flink-streaming-java"))

    implementation(Libs.jsr305)

    shade("com.google.protobuf:protobuf-java")
    shade("com.amazonaws:aws-java-sdk-kinesis:${stringProperty("aws.sdk.version")}")
    shade("com.amazonaws:aws-java-sdk-sts:${stringProperty("aws.sdk.version")}")
    shade("com.amazonaws:amazon-kinesis-producer:${stringProperty("aws.kinesis-kpl.version")}")
    shade("com.amazonaws:amazon-kinesis-client:${stringProperty("aws.kinesis-kcl.version")}")
    shade("com.amazonaws:dynamodb-streams-kinesis-adapter:${stringProperty("aws.dynamodbstreams-kinesis-adapter.version")}")

    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-tests", configuration = TEST_JAR))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)

    configurations.all {
        exclude(group = "com.amazonaws", module ="aws-java-sdk-cloudwatch")
        exclude(group = "software.amazon.ion")
    }

}

description = "flink-connector-kinesis"

flinkDependencyManagement {
    dependency("org.apache.httpcomponents:httpclient", version = "4.5.9")
    dependency(Libs.guava, version = stringProperty("guava.version"))
}


tasks.withType<ShadowJar> {
    dependencies {
        include(dependency("com.amazonaws:"))
        include(dependency("com.google.protobuf:"))
        include(dependency("org.apache.httpcomponents:"))
        // Java 11 specific inclusion; should be a no-op for other versions
        include(dependency("javax.xml.bind:jaxb-api"))
    }
    relocate("com.google.protobuf", "org.apache.flink.kinesis.shaded.com.google.protobuf")
    relocate("com.amazonaws", "org.apache.flink.kinesis.shaded.com.amazonaws")
    relocate("org.apache.http", "org.apache.flink.kinesis.shaded.org.apache.http")
}
