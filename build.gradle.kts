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
    id("org.nosphere.apache.rat") version "0.5.2"
    `java-library`
}

if (!gradle.startParameter.isOffline && isCiServer) {
    configure<com.gradle.scan.plugin.BuildScanExtension> {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

allprojects {
    group = "org.apache.flink"
    version = "1.10-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    if(project.subprojects.isNotEmpty()) {
        return@subprojects
    }
    apply(plugin = "java-library")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    flinkRegisterTestApi()

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<ScalaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.named<Test>("test") {
        useJUnit()
        systemProperty("log4j.configuration", "log4j-test.properties")
        maxParallelForks = gradle.startParameter.maxWorkerCount

        jvmArgs("-Xms256m", "-Xmx2048m", "-XX:+UseG1GC")
        ignoreFailures = true

        filter {
            includeTestsMatching("*Test")
            isFailOnNoMatchingTests = false
        }
    }
    val integrationTest by tasks.registering(Test::class) {
        description = "Runs integration tests."
        group = "verification"

        useJUnit()
        systemProperty("log4j.configuration", "log4j-test.properties")
        maxParallelForks = gradle.startParameter.maxWorkerCount

        jvmArgs("-Xms256m", "-Xmx2048m", "-XX:+UseG1GC")
        ignoreFailures = true
        shouldRunAfter("test")

        filter {
            includeTestsMatching("*ITCase")
            isFailOnNoMatchingTests = false
        }
    }
    tasks.check {
        dependsOn(integrationTest)
    }

    flinkSetupPublishing()

    flinkDependencyManagement {
        dependency(Libs.flink_shaded_asm_7, version = "7.1-${stringProperty("flink.shaded.version")}")

        dependency(Libs.flink_shaded_guava, version = "18.0-${stringProperty("flink.shaded.version")}")

        dependencyGroup(version = "${stringProperty("jackson.version")}-${stringProperty("flink.shaded.version")}") {
            dependency(Libs.flink_shaded_jackson)

            dependency(Libs.flink_shaded_jackson_module_jsonschema)
        }

        dependency(Libs.flink_shaded_netty, version = "4.1.39.Final-${stringProperty("flink.shaded.version")}")

        dependency(Libs.flink_shaded_netty_tcnative_dynamic, version = "2.0.25.Final-${stringProperty("flink.shaded.version")}")

        //   This manages the 'javax.annotation' annotations (JSR305)
        dependency(Libs.jsr305, version = "1.3.9")

        dependencyGroup(version = stringProperty("slf4j.version")) {
            dependency(Libs.slf4j_api)

            dependency(Libs.slf4j_log4j12)
        }

        dependency(Libs.log4j, version = stringProperty("log4j.version"))

        dependency(Libs.commons_lang3, version = "3.3.2")

        dependency(Libs.snappy_java, version = "1.1.4")

        dependency(Libs.oshi_core, version = "3.4.0")

        //   Make sure we use a consistent avro version between Flink and Hadoop
        dependency(Libs.avro, version = stringProperty("avro.version"))

        //   For dependency convergence
        dependency("org.hamcrest:hamcrest-core", version = stringProperty("hamcrest.version"))

        //   mockito/powermock mismatch
        dependency("net.bytebuddy:byte-buddy", version = "1.8.15")

        //   mockito/powermock mismatch
        dependency("net.bytebuddy:byte-buddy-agent", version = "1.8.15")

        //   For dependency convergence
        dependency("org.objenesis:objenesis", version = "2.1")

        //   For dependency convergence
        dependency("com.typesafe:config", version = "1.3.0")

        //   For dependency convergence
        dependency("commons-logging:commons-logging", version = "1.1.3")

        //   For dependency convergence
        dependency(Libs.junit, version = stringProperty("junit.version"))

        //   For dependency convergence
        dependency("org.tukaani:xz", version = "1.5")

        //   Make sure we use a consistent commons-cli version throughout the project
        dependency(Libs.commons_cli, version = "1.3.1")

        dependency(Libs.commons_io, version = "2.4")

        //   commons collections needs version be pinned version this critical security fix version
        dependency(Libs.commons_collections, version = "3.2.2")

        /* We have version bump the commons-configuration version version 1.7 because Hadoop uses per
        default 1.6. This version has the problem that it depends on commons-beanutils-core and
        commons-digester. Commons-digester depends on commons-beanutils. Both dependencies are
        contains classes of commons-collections. Since the dependency reduced pom does not
        exclude commons-beanutils from commons-configuration, sbt would pull it in again. The
        solution is setting the version of commons-configuration version 1.7 which also depends on
        common-beanutils. Consequently, the dependency reduced pom will also contain an
        exclusion for commons-beanutils for commons-configuration.  */
        dependency("commons-configuration:commons-configuration", version = "1.7")

        dependency("commons-codec:commons-codec", version = "1.10")

        dependency(Libs.commons_math3, version = "3.5")

        dependency(Libs.commons_compress, version = "1.18")

        //   Managed dependency required for HBase in flink-hbase
        dependency(Libs.javassist, version = "3.24.0-GA")

        //   joda time is pulled in different versions by different transitive dependencies
        dependency(Libs.joda_time, version = "2.5")

        dependency(Libs.joda_convert, version = "1.7")

        //   kryo used in different versions by Flink an chill
        dependency(Libs.kryo, version = "2.24.0")

        dependency(Libs.scala_library, version = stringProperty("scala.version"))

        dependency(Libs.scala_reflect, version = stringProperty("scala.version"))

        dependency(Libs.scala_compiler, version = stringProperty("scala.version"))

        dependency(Libs.grizzled_slf4j, version = "1.3.2")

        dependency(Libs.akka_actor, version = stringProperty("akka.version"))

        dependency(Libs.akka_remote, version = stringProperty("akka.version")) {
            exclude(group = "io.aeron", module = "aeron-driver")
            exclude(group = "io.aeron", module = "aeron-client")
        }

        /*  Transitive dependency of akka-remote that we explicitly define version keep it
            visible after the shading (without relocation!) of akka-remote  */
        dependency(Libs.akka_stream, version = stringProperty("akka.version"))

        /*  Transitive dependency of akka-remote that we explicitly define version keep it
            visible after the shading (without relocation!) of akka-remote  */
        dependency(Libs.akka_protobuf, version = stringProperty("akka.version"))

        dependency(Libs.akka_slf4j, version = stringProperty("akka.version"))

        dependency("com.typesafe.akka:akka-camel_${scalaMinorVersion}", version = stringProperty("akka.version"))

        dependency(Libs.scala_parser_combinators, version = "1.1.1")

        dependency(Libs.akka_testkit, version = stringProperty("akka.version"), configuration = "testImplementation")

        dependency(Libs.scalatest, version = "3.0.0", configuration = "testImplementation")

        dependency(Libs.scopt, version = "3.5.0") {
            exclude(group = "org.scala-lang", module = "scala-library")
        }

        dependency(Libs.zookeeper, version = stringProperty("zookeeper.version")) {
            exclude(group = "log4j", module = "log4j")
            exclude(group = "org.slf4j", module = "slf4j-log4j12")
            //   Netty is only needed for ZK servers, not clients
            exclude(group = "io.netty", module = "netty")
            exclude(group = "jline", module = "jline")
        }

        /*  We have version define the versions for httpcore and httpclient here such that a consistent
         version is used by the shaded hadoop jars and the flink-yarn-test project because of MNG-5899.

         See FLINK-6836 for more details  */
        dependency("org.apache.httpcomponents:httpcore", version = "4.4.6")

        dependency("org.apache.httpcomponents:httpclient", version = "4.5.3")

        dependency(Libs.reflections, version = "0.9.10", configuration = "testImplementation")

        dependency(Libs.hadoop_common, version = stringProperty("hadoop.version"))

        dependency(Libs.flink_shaded_hadoop_2, version = "${stringProperty("hadoop.version")}-${stringProperty("flink.shaded.version")}")

        dependency(Libs.chill, version = stringProperty("chill.version"))

        dependencyGroup(stringProperty("powermock.version")) {
            dependency(Libs.powermock_module_junit4, configuration = "testImplementation")
            dependency(Libs.powermock_api_mockito2, configuration = "testImplementation") {
                exclude(Libs.mockito_core)
            }
        }

        dependency(Libs.hamcrest_all, version = stringProperty("hamcrest.version"), configuration = "testImplementation")

        dependency(Libs.mockito_core, version = stringProperty("mockito.version"), configuration = "testImplementation")
    }
}

flinkSetupScalaProjects()

tasks.rat {
    file("$rootDir/.gitignore").forEachLine { exclude(it) }
    file("$rootDir/tools/rat.excludes").useLines { lines ->
        lines.filterNot { it.startsWith("#") || it.isEmpty() }
                .forEach { exclude(it) }
    }
}
