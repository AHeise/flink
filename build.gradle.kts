import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.staticProperties

plugins {
    id("de.fayard.buildSrcVersions") version "0.4.2"
    id("org.nosphere.apache.rat") version "0.5.2"
}

allprojects {
    group = "org.apache.flink"
    version = "1.10-SNAPSHOT"
}

if (!gradle.startParameter.isOffline) {
    configure<com.gradle.scan.plugin.BuildScanExtension> {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

subprojects {
    if(project.subprojects.isNotEmpty()) {
        return@subprojects
    }
    apply(plugin = "java-library")

    flinkRegisterTestApi()

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<ScalaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<Test>().configureEach {
        useJUnit()
        systemProperty("log4j.configuration", "log4j-test.properties")
//        maxParallelForks = 24

        jvmArgs("-Xms256m", "-Xmx2048m", "-XX:+UseG1GC")
        include("**/*Test.*")
        ignoreFailures = true
    }

    flinkSetupPublishing()

    flinkDependencyManagement {
        "api"(Libs.flink_shaded_asm_7, version = "7.1-${stringProperty("flink.shaded.version")}")

        "api"(Libs.flink_shaded_guava, version = "18.0-${stringProperty("flink.shaded.version")}")

        flinkDependencyGroup(version = "${stringProperty("jackson.version")}-${stringProperty("flink.shaded.version")}") {
            "api"(Libs.flink_shaded_jackson)

            "api"(Libs.flink_shaded_jackson_module_jsonschema)
        }

        "api"(Libs.flink_shaded_netty, version = "4.1.39.Final-${stringProperty("flink.shaded.version")}")

        "testImplementation"(Libs.flink_shaded_netty_tcnative_dynamic, version = "2.0.25.Final-${stringProperty("flink.shaded.version")}")

        //   This manages the 'javax.annotation' annotations (JSR305)
        "api"(Libs.jsr305, version = "1.3.9")

        flinkDependencyGroup(version = stringProperty("slf4j.version")) {
            "api"(Libs.slf4j_api)

            "api"(Libs.slf4j_log4j12)
        }

        "api"(Libs.log4j, version = stringProperty("log4j.version"))

        "api"(Libs.commons_lang3, version = "3.3.2")

        "api"(Libs.snappy_java, version = "1.1.4")

        "api"(Libs.oshi_core, version = "3.4.0")

        //   Make sure we use a consistent avro version between Flink and Hadoop
        "api"(Libs.avro, version = stringProperty("avro.version"))

        //   For dependency convergence
        "api"("org.hamcrest:hamcrest-core", version = stringProperty("hamcrest.version"))

        //   mockito/powermock mismatch
        "api"("net.bytebuddy:byte-buddy", version = "1.8.15")

        //   mockito/powermock mismatch
        "api"("net.bytebuddy:byte-buddy-agent", version = "1.8.15")

        //   For dependency convergence
        "api"("org.objenesis:objenesis", version = "2.1")

        //   For dependency convergence
        "api"("com.typesafe:config", version = "1.3.0")

        //   For dependency convergence
        "api"("commons-logging:commons-logging", version = "1.1.3")

        //   For dependency convergence
        "api"(Libs.junit, version = stringProperty("junit.version"))

        //   For dependency convergence
        "api"("org.tukaani:xz", version = "1.5")

        //   Make sure we use a consistent commons-cli version throughout the project
        "api"(Libs.commons_cli, version = "1.3.1")

        "api"(Libs.commons_io, version = "2.4")

        //   commons collections needs version be pinned version this critical security fix version
        "api"(Libs.commons_collections, version = "3.2.2")

        /* We have version bump the commons-configuration version version 1.7 because Hadoop uses per
        default 1.6. This version has the problem that it depends on commons-beanutils-core and
        commons-digester. Commons-digester depends on commons-beanutils. Both dependencies are
        contains classes of commons-collections. Since the dependency reduced pom does not
        exclude commons-beanutils from commons-configuration, sbt would pull it in again. The
        solution is setting the version of commons-configuration version 1.7 which also depends on
        common-beanutils. Consequently, the dependency reduced pom will also contain an
        exclusion for commons-beanutils for commons-configuration.  */
        "api"("commons-configuration:commons-configuration", version = "1.7")

        "api"("commons-codec:commons-codec", version = "1.10")

        "api"(Libs.commons_math3, version = "3.5")

        "api"(Libs.commons_compress, version = "1.18")

        //   Managed dependency required for HBase in flink-hbase
        "api"(Libs.javassist, version = "3.24.0-GA")

        //   joda time is pulled in different versions by different transitive dependencies
        "api"(Libs.joda_time, version = "2.5")

        "api"(Libs.joda_convert, version = "1.7")

        //   kryo used in different versions by Flink an chill
        "api"(Libs.kryo, version = "2.24.0")

        "api"(Libs.scala_library, version = stringProperty("scala.version"))

        "api"(Libs.scala_reflect, version = stringProperty("scala.version"))

        "api"(Libs.scala_compiler, version = stringProperty("scala.version"))

        "api"(Libs.grizzled_slf4j, version = "1.3.2")

        "api"(Libs.akka_actor, version = stringProperty("akka.version"))

        "api"(Libs.akka_remote, version = stringProperty("akka.version")) {
            exclude(group = "io.aeron", module = "aeron-driver")
            exclude(group = "io.aeron", module = "aeron-client")
        }

        /*  Transitive dependency of akka-remote that we explicitly define version keep it
            visible after the shading (without relocation!) of akka-remote  */
        "api"(Libs.akka_stream, version = stringProperty("akka.version"))

        /*  Transitive dependency of akka-remote that we explicitly define version keep it
            visible after the shading (without relocation!) of akka-remote  */
        "api"(Libs.akka_protobuf, version = stringProperty("akka.version"))

        "api"(Libs.akka_slf4j, version = stringProperty("akka.version"))

        "api"("com.typesafe.akka:akka-camel_${scalaMinorVersion}", version = stringProperty("akka.version"))

        "api"(Libs.scala_parser_combinators, version = "1.1.1")

        "testImplementation"(Libs.akka_testkit, version = stringProperty("akka.version"))

        "testImplementation"(Libs.scalatest, version = "3.0.0")

        "api"(Libs.scopt, version = "3.5.0") {
            exclude(group = "org.scala-lang", module = "scala-library")
        }

        "api"(Libs.zookeeper, version = stringProperty("zookeeper.version")) {
            exclude(group = "log4j", module = "log4j")
            exclude(group = "org.slf4j", module = "slf4j-log4j12")
            //   Netty is only needed for ZK servers, not clients
            exclude(group = "io.netty", module = "netty")
            exclude(group = "jline", module = "jline")
        }

        /*  We have version define the versions for httpcore and httpclient here such that a consistent
         version is used by the shaded hadoop jars and the flink-yarn-test project because of MNG-5899.

         See FLINK-6836 for more details  */
        "api"("org.apache.httpcomponents:httpcore", version = "4.4.6")

        "api"("org.apache.httpcomponents:httpclient", version = "4.5.3")

        "testImplementation"(Libs.reflections, version = "0.9.10")

        "api"(Libs.hadoop_common, version = stringProperty("hadoop.version"))

        "api"(Libs.flink_shaded_hadoop_2, version = "${stringProperty("hadoop.version")}-${stringProperty("flink.shaded.version")}")
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