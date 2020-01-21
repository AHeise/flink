import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.kotlin.dsl.extra
import kotlin.String

val Project.Libs
    get() = rootProject.extra.getOrPut("Libs") { Libs(rootProject) }

class Libs(val project: Project) {
    val scalaMinorVersion = project.scalaMinorVersion

    companion object {
        fun dependency(group: String, name: String): ExternalModuleDependency =
                DefaultExternalModuleDependency(group, name, "")
    }

    /**
     * https://github.com/sgroschupf/zkclient
     */
    val zkclient = Libs.dependency(group = "com.101tec", name = "zkclient") // .zkclient

    /**
     * http://www.aliyun.com/product/oss
     */
    val aliyun_sdk_oss = Libs.dependency(group = "com.aliyun.oss", name = "aliyun-sdk-oss") // .aliyun_sdk_oss

    /**
     * https://aws.amazon.com/sdkforjava
     */
    val aws_java_sdk_core = Libs.dependency(group = "com.amazonaws", name = "aws-java-sdk-core") //
        // .aws_java_sdk_core

    /**
     * https://aws.amazon.com/sdkforjava
     */
    val aws_java_sdk_dynamodb = Libs.dependency(group = "com.amazonaws", name = "aws-java-sdk-dynamodb") //
        // .aws_java_sdk_dynamodb

    /**
     * https://aws.amazon.com/sdkforjava
     */
    val aws_java_sdk_kms = Libs.dependency(group = "com.amazonaws", name = "aws-java-sdk-kms") // .aws_java_sdk_kms

    /**
     * https://aws.amazon.com/sdkforjava
     */
    val aws_java_sdk_s3 = Libs.dependency(group = "com.amazonaws", name = "aws-java-sdk-s3") // .aws_java_sdk_s3

    /**
     * https://github.com/dataArtisans/frocksdb
     */
    val frocksdbjni = Libs.dependency(group = "com.data-artisans", name = "frocksdbjni") // .frocksdbjni

    /**
     * https://github.com/datastax/java-driver
     */
    val cassandra_driver_core = Libs.dependency(group = "com.datastax.cassandra", name = "cassandra-driver-core") //
        // .com_datastax_cassandra

    /**
     * https://github.com/datastax/java-driver
     */
    val cassandra_driver_mapping = Libs.dependency(group = "com.datastax.cassandra", name = "cassandra-driver-mapping") //
        // .com_datastax_cassandra

    /**
     * https://github.com/EsotericSoftware/kryo
     */
    val kryo = Libs.dependency(group = "com.esotericsoftware.kryo", name = "kryo") // .kryo

    /**
     * https://github.com/facebook/presto-hadoop-apache2
     */
    val hadoop_apache2 = Libs.dependency(group = "com.facebook.presto.hadoop", name = "hadoop-apache2") //
        // .hadoop_apache2

    /**
     * https://github.com/facebook/presto
     */
    val presto_hive = Libs.dependency(group = "com.facebook.presto", name = "presto-hive") // .presto_hive

    /**
     * https://github.com/oshi/oshi
     */
    val oshi_core = Libs.dependency(group = "com.github.oshi", name = "oshi-core") // .oshi_core

    /**
     * https://github.com/scopt/scopt
     */
    val scopt = Libs.dependency(group = "com.github.scopt", name = "scopt_${scalaMinorVersion}") // .scopt

    /**
     * http://wiremock.org
     */
    val wiremock = Libs.dependency(group = "com.github.tomakehurst", name = "wiremock") // .wiremock

    /**
     * https://github.com/googleapis/google-cloud-java/tree/master/google-cloud-clients/google-cloud-pubsub
     */
    val google_cloud_pubsub = Libs.dependency(group = "com.google.cloud", name = "google-cloud-pubsub") //
        // .google_cloud_pubsub

    /**
     * http://findbugs.sourceforge.net/
     */
    val jsr305 = Libs.dependency(group = "com.google.code.findbugs", name = "jsr305") // .jsr305

    /**
     * https://github.com/google/guava
     */
    val guava = Libs.dependency(group = "com.google.guava", name = "guava") // .guava

    /**
     * https://github.com/klarna/HiveRunner
     */
    val hiverunner = Libs.dependency(group = "com.klarna", name = "hiverunner") // .hiverunner

    /**
     * http://unirest.io/
     */
    val unirest_java = Libs.dependency(group = "com.mashape.unirest", name = "unirest-java") // .unirest_java

    /**
     * https://github.com/Azure/azure-sdk-for-java
     */
    val azure = Libs.dependency(group = "com.microsoft.azure", name = "azure") // .azure

    /**
     * https://github.com/Netflix/Fenzo
     */
    val fenzo_core = Libs.dependency(group = "com.netflix.fenzo", name = "fenzo-core") // .fenzo_core

    /**
     * https://www.rabbitmq.com
     */
    val amqp_client = Libs.dependency(group = "com.rabbitmq", name = "amqp-client") // .amqp_client

    /**
     * https://github.com/spotify/docker-client
     */
    val docker_client = Libs.dependency(group = "com.spotify", name = "docker-client") // .docker_client

    /**
     * https://github.com/square/okhttp
     */
    val okhttp = Libs.dependency(group = "com.squareup.okhttp3", name = "okhttp") // .okhttp

    /**
     * https://github.com/twitter/chill
     */
    val chill = Libs.dependency(group = "com.twitter", name = "chill_${scalaMinorVersion}") // .chill

    /**
     * http://github.com/twitter/hbc
     */
    val hbc_core = Libs.dependency(group = "com.twitter", name = "hbc-core") // .hbc_core

    /**
     * https://akka.io/
     */
    val akka_actor = Libs.dependency(group = "com.typesafe.akka", name = "akka-actor_${scalaMinorVersion}") //
        // .com_typesafe_akka

    /**
     * https://akka.io/
     */
    val akka_protobuf = Libs.dependency(group = "com.typesafe.akka", name = "akka-protobuf_${scalaMinorVersion}") //
        // .com_typesafe_akka

    /**
     * https://akka.io/
     */
    val akka_remote = Libs.dependency(group = "com.typesafe.akka", name = "akka-remote_${scalaMinorVersion}") //
        // .com_typesafe_akka

    /**
     * https://akka.io/
     */
    val akka_slf4j = Libs.dependency(group = "com.typesafe.akka", name = "akka-slf4j_${scalaMinorVersion}") //
        // .com_typesafe_akka

    /**
     * https://akka.io/
     */
    val akka_stream = Libs.dependency(group = "com.typesafe.akka", name = "akka-stream_${scalaMinorVersion}") //
        // .com_typesafe_akka

    /**
     * https://akka.io/
     */
    val akka_testkit = Libs.dependency(group = "com.typesafe.akka", name = "akka-testkit_${scalaMinorVersion}") //
        // .com_typesafe_akka

    /**
     * http://commons.apache.org/proper/commons-cli/
     */
    val commons_cli = Libs.dependency(group = "commons-cli", name = "commons-cli") // .commons_cli

    val commons_collections = Libs.dependency(group = "commons-collections", name = "commons-collections") //
        // .commons_collections

    /**
     * http://commons.apache.org/proper/commons-io/
     */
    val commons_io = Libs.dependency(group = "commons-io", name = "commons-io") // .commons_io


    /**
     * https://github.com/airlift/tpch
     */
    val tpch = Libs.dependency(group = "io.airlift.tpch", name = "tpch") // .tpch

    /**
     * http://confluent.io
     */
    val kafka_schema_registry_client = Libs.dependency(group = "io.confluent", name = "kafka-schema-registry-client") //
        // .kafka_schema_registry_client

    /**
     * http://metrics.dropwizard.io
     */
    val metrics_core = Libs.dependency(group = "io.dropwizard.metrics", name = "metrics-core") //
        // .io_dropwizard_metrics

    /**
     * http://metrics.dropwizard.io
     */
    val metrics_graphite = Libs.dependency(group = "io.dropwizard.metrics", name = "metrics-graphite") //
        // .io_dropwizard_metrics

    /**
     * http://netty.io/
     */
    val netty = Libs.dependency(group = "io.netty", name = "netty") // .netty

    /**
     * http://github.com/prometheus/client_java
     */
    val simpleclient = Libs.dependency(group = "io.prometheus", name = "simpleclient") // .io_prometheus

    /**
     * http://github.com/prometheus/client_java
     */
    val simpleclient_httpserver = Libs.dependency(group = "io.prometheus", name = "simpleclient_httpserver") //
        // .io_prometheus

    /**
     * http://github.com/prometheus/client_java
     */
    val simpleclient_pushgateway = Libs.dependency(group = "io.prometheus", name = "simpleclient_pushgateway") //
        // .io_prometheus

    /**
     * http://fastutil.di.unimi.it/
     */
    val fastutil = Libs.dependency(group = "it.unimi.dsi", name = "fastutil") // .fastutil

    /**
     * https://www.joda.org/joda-time/
     */
    val joda_time = Libs.dependency(group = "joda-time", name = "joda-time") // .joda_time

    /**
     * http://junit.org
     */
    val junit = Libs.dependency(group = "junit", name = "junit") // .junit

    /**
     * http://logging.apache.org/log4j/1.2/
     */
    val log4j = Libs.dependency(group = "log4j", name = "log4j") // .log4j

    /**
     * https://github.com/irmen/Pyrolite
     */
    val pyrolite = Libs.dependency(group = "net.razorvine", name = "pyrolite") // .pyrolite

    val py4j = Libs.dependency(group = "net.sf.py4j", name = "py4j") // .py4j

    /**
     * http://avro.apache.org
     */
    val avro = Libs.dependency(group = "org.apache.avro", name = "avro") // .avro

    /**
     * https://calcite.apache.org/avatica
     */
    val avatica_core = Libs.dependency(group = "org.apache.calcite.avatica", name = "avatica-core") //
        // .avatica_core

    /**
     * https://calcite.apache.org
     */
    val calcite_core = Libs.dependency(group = "org.apache.calcite", name = "calcite-core") // .calcite_core

    /**
     * http://cassandra.apache.org
     */
    val cassandra_all = Libs.dependency(group = "org.apache.cassandra", name = "cassandra-all") // .cassandra_all

    /**
     * https://commons.apache.org/proper/commons-compress/
     */
    val commons_compress = Libs.dependency(group = "org.apache.commons", name = "commons-compress") //
        // .commons_compress

    /**
     * http://commons.apache.org/proper/commons-lang/
     */
    val commons_lang3 = Libs.dependency(group = "org.apache.commons", name = "commons-lang3") // .commons_lang3

    /**
     * http://commons.apache.org/proper/commons-math/
     */
    val commons_math3 = Libs.dependency(group = "org.apache.commons", name = "commons-math3") // .commons_math3

    /**
     * http://curator.apache.org
     */
    val curator_recipes = Libs.dependency(group = "org.apache.curator", name = "curator-recipes") //
        // .org_apache_curator

    /**
     * http://curator.apache.org
     */
    val curator_test = Libs.dependency(group = "org.apache.curator", name = "curator-test") // .org_apache_curator

    /**
     * http://db.apache.org/derby/
     */
    val derby = Libs.dependency(group = "org.apache.derby", name = "derby") // .derby

    /**
     * http://flink.apache.org
     */

    val flink_shaded_asm_7 = Libs.dependency(group = "org.apache.flink", name = "flink-shaded-asm-7") //
        // .flink_shaded_asm_7

    /**
     * http://flink.apache.org
     */
    val flink_shaded_guava = Libs.dependency(group = "org.apache.flink", name = "flink-shaded-guava") //
        // .flink_shaded_guava

    /**
     * http://flink.apache.org
     */
    val flink_shaded_hadoop_2 = Libs.dependency(group = "org.apache.flink", name = "flink-shaded-hadoop-2") //
        // .flink_shaded_hadoop_2

    /**
     * http://flink.apache.org
     */
    val flink_shaded_jackson_module_jsonschema: String =
        "org.apache.flink:flink-shaded-jackson-module-jsonSchema" //
        // .flink_shaded_jackson_module_jsonschema

    /**
     * http://flink.apache.org
     */
    val flink_shaded_jackson = Libs.dependency(group = "org.apache.flink", name = "flink-shaded-jackson") //
        // .flink_shaded_jackson

    /**
     * http://flink.apache.org
     */
    val flink_shaded_netty_tcnative_dynamic: String =
        "org.apache.flink:flink-shaded-netty-tcnative-dynamic" //
        // .flink_shaded_netty_tcnative_dynamic

    /**
     * http://flink.apache.org
     */
    val flink_shaded_netty = Libs.dependency(group = "org.apache.flink", name = "flink-shaded-netty") //
        // .flink_shaded_netty

    val hadoop_aliyun = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-aliyun") // .hadoop_aliyun

    val hadoop_aws = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-aws") // .hadoop_aws

    val hadoop_azure = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-azure") // .hadoop_azure

    val hadoop_client = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-client") // .hadoop_client

    val hadoop_common = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-common") // .hadoop_common

    val hadoop_hdfs = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-hdfs") // .hadoop_hdfs

    val hadoop_mapreduce_client_core: String =
        "org.apache.hadoop:hadoop-mapreduce-client-core" // .hadoop_mapreduce_client_core

    val hadoop_minicluster = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-minicluster") //
        // .hadoop_minicluster

    val hadoop_minikdc = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-minikdc") // .hadoop_minikdc

    val hadoop_openstack = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-openstack") //
        // .hadoop_openstack

    val hadoop_yarn_api = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-yarn-api") // .hadoop_yarn_api

    val hadoop_yarn_client = Libs.dependency(group = "org.apache.hadoop", name = "hadoop-yarn-client") //
        // .hadoop_yarn_client

    /**
     * https://hbase.apache.org
     */
    val hbase_hadoop_compat = Libs.dependency(group = "org.apache.hbase", name = "hbase-hadoop-compat") //
        // .org_apache_hbase

    /**
     * https://hbase.apache.org
     */
    val hbase_hadoop2_compat = Libs.dependency(group = "org.apache.hbase", name = "hbase-hadoop2-compat") //
        // .org_apache_hbase

    /**
     * https://hbase.apache.org
     */
    val hbase_server = Libs.dependency(group = "org.apache.hbase", name = "hbase-server") // .org_apache_hbase

      /**
       * https://hbase.apache.org
       */
      val hbase_client = Libs.dependency(group = "org.apache.hbase", name = "hbase-client") // .org_apache_hbase

      /**
       * https://hbase.apache.org
       */
      val hbase_common = Libs.dependency(group = "org.apache.hbase", name = "hbase-common") // .org_apache_hbase

    /**
     * http://maven.apache.org
     */
    val hcatalog_core = Libs.dependency(group = "org.apache.hive.hcatalog", name = "hcatalog-core") //
        // .hcatalog_core

    /**
     * http://hive.apache.org
     */
    val hive_hcatalog_core = Libs.dependency(group = "org.apache.hive.hcatalog", name = "hive-hcatalog-core") //
        // .hive_hcatalog_core

    /**
     * http://hive.apache.org
     */
    val hive_exec = Libs.dependency(group = "org.apache.hive", name = "hive-exec") // .org_apache_hive

    /**
     * http://hive.apache.org
     */
    val hive_metastore = Libs.dependency(group = "org.apache.hive", name = "hive-metastore") // .org_apache_hive

    /**
     * http://hive.apache.org
     */
    val hive_service = Libs.dependency(group = "org.apache.hive", name = "hive-service") // .org_apache_hive

    /**
     * https://kafka.apache.org
     */
    val kafka_clients = Libs.dependency(group = "org.apache.kafka", name = "kafka-clients") // .kafka_clients

    /**
     * https://kafka.apache.org
     */
    val kafka = Libs.dependency(group = "org.apache.kafka", name = "kafka_${scalaMinorVersion}") // .kafka

    /**
     * https://logging.apache.org/log4j/2.x/
     */
    val log4j_core = Libs.dependency(group = "org.apache.logging.log4j", name = "log4j-core") // .log4j_core

    /**
     * https://logging.apache.org/log4j/2.x/
     */
    val log4j_to_slf4j = Libs.dependency(group = "org.apache.logging.log4j", name = "log4j-to-slf4j") //
        // .log4j_to_slf4j

    /**
     * http://mesos.apache.org
     */
    val mesos = Libs.dependency(group = "org.apache.mesos", name = "mesos") // .mesos

    val netlib_core = Libs.dependency(group = "com.github.fommil.netlib", name = "core") // .netlib_core

    /**
     * http://nifi.apache.org
     */
    val nifi_site_to_site_client = Libs.dependency(group = "org.apache.nifi", name = "nifi-site-to-site-client") //
        // .nifi_site_to_site_client

    /**
     * http://orc.apache.org
     */
    val orc_core = Libs.dependency(group = "org.apache.orc", name = "orc-core") // .orc_core

    /**
     * https://parquet.apache.org
     */
    val parquet_avro = Libs.dependency(group = "org.apache.parquet", name = "parquet-avro") // .org_apache_parquet

    /**
     * https://parquet.apache.org
     */
    val parquet_hadoop = Libs.dependency(group = "org.apache.parquet", name = "parquet-hadoop") //
        // .org_apache_parquet

    /**
     * http://zookeeper.apache.org
     */
    val zookeeper = Libs.dependency(group = "org.apache.zookeeper", name = "zookeeper") // .zookeeper

    /**
     * http://software.clapper.org/grizzled-slf4j/
     */
    val grizzled_slf4j = Libs.dependency(group = "org.clapper", name = "grizzled-slf4j_${scalaMinorVersion}") //
        // .grizzled_slf4j

    /**
     * http://janino-compiler.github.io/janino/
     */
    val janino = Libs.dependency(group = "org.codehaus.janino", name = "janino") // .janino

    /**
     * https://github.com/elastic/elasticsearch
     */
    val elasticsearch_rest_high_level_client: String =
        "org.elasticsearch.client:elasticsearch-rest-high-level-client" //
        // .org_elasticsearch_client

    /**
     * https://github.com/elastic/elasticsearch
     */
    val transport = Libs.dependency(group = "org.elasticsearch.client", name = "transport") //
        // .org_elasticsearch_client

    /**
     * https://github.com/elastic/elasticsearch
     */
    val transport_netty3_client = Libs.dependency(group = "org.elasticsearch.plugin", name = "transport-netty3-client") //
            // .org_elasticsearch_plugin

    /**
     * https://github.com/elastic/elasticsearch
     */
    val transport_netty4_client = Libs.dependency(group = "org.elasticsearch.plugin", name = "transport-netty4-client") //
        // .org_elasticsearch_plugin

    /**
     * https://github.com/elastic/elasticsearch
     */
    val elasticsearch = Libs.dependency(group = "org.elasticsearch", name = "elasticsearch") // .elasticsearch

    /**
     * https://github.com/hamcrest/JavaHamcrest
     */
    val hamcrest_all = Libs.dependency(group = "org.hamcrest", name = "hamcrest-all") // .hamcrest_all

    /**
     * http://www.influxdb.org
     */
    val influxdb_java = Libs.dependency(group = "org.influxdb", name = "influxdb-java") // .influxdb_java

    /**
     * http://www.javassist.org/
     */
    val javassist = Libs.dependency(group = "org.javassist", name = "javassist") // .javassist

    val jcip_annotations = Libs.dependency(group = "net.jcip", name = "jcip-annotations") // .jcip_annotations

    val jline_reader = Libs.dependency(group = "org.jline", name = "jline-reader") // .org_jline

    val jline_terminal = Libs.dependency(group = "org.jline", name = "jline-terminal") // .org_jline

    /**
     * https://www.joda.org/${joda.artifactId}/
     */
    val joda_convert = Libs.dependency(group = "org.joda", name = "joda-convert") // .joda_convert

    /**
     * https://jsoup.org/
     */
    val jsoup = Libs.dependency(group = "org.jsoup", name = "jsoup") // .jsoup

    /**
     * https://github.com/lz4/lz4-java
     */
    val lz4_java = Libs.dependency(group = "org.lz4", name = "lz4-java") // .lz4_java

    /**
     * https://github.com/mockito/mockito
     */
    val mockito_core = Libs.dependency(group = "org.mockito", name = "mockito-core") // .mockito_core

    /**
     * http://www.powermock.org
     */
    val powermock_api_mockito2 = Libs.dependency(group = "org.powermock", name = "powermock-api-mockito2") //
        // .org_powermock

    /**
     * http://www.powermock.org
     */
    val powermock_module_junit4 = Libs.dependency(group = "org.powermock", name = "powermock-module-junit4") //
        // .org_powermock

    /**
     * https://projectlombok.org
     */
    val lombok = Libs.dependency(group = "org.projectlombok", name = "lombok") // .lombok

    /**
     * http://github.com/ronmamo/reflections
     */
    val reflections = Libs.dependency(group = "org.reflections", name = "reflections") // .reflections

    /**
     * https://www.scala-lang.org/
     */
    val scala_compiler = Libs.dependency(group = "org.scala-lang", name = "scala-compiler") // .org_scala_lang

    /**
     * https://www.scala-lang.org/
     */
    val scala_library = Libs.dependency(group = "org.scala-lang", name = "scala-library") // .org_scala_lang

    val scala_r = Libs.dependency(group = "org.scala-lang", name = "scala-r") // .org_scala_lang

    val scala_parser_combinators = Libs.dependency(group = "org.scala-lang.modules", name = "scala-parser-combinators_${scalaMinorVersion}") // .scala_parser_combinators

    /**
     * https://www.scala-lang.org/
     */
    val scala_reflect = Libs.dependency(group = "org.scala-lang", name = "scala-reflect") // .org_scala_lang

    /**
     * http://www.scalatest.org
     */
    val scalatest = Libs.dependency(group = "org.scalatest", name = "scalatest_${scalaMinorVersion}") // .org_scalatest

    /**
     * http://moepii.sourceforge.net/
     */
    val irclib = Libs.dependency(group = "org.schwering", name = "irclib") // .irclib

    /**
     * http://www.slf4j.org
     */
    val slf4j_api = Libs.dependency(group = "org.slf4j", name = "slf4j-api") // .org_slf4j

    /**
     * http://www.slf4j.org
     */
    val slf4j_log4j12 = Libs.dependency(group = "org.slf4j", name = "slf4j-log4j12") // .org_slf4j

    /**
     * https://github.com/xerial/snappy-java
     */
    val snappy_java = Libs.dependency(group = "org.xerial.snappy", name = "snappy-java") // .snappy_java
  }
