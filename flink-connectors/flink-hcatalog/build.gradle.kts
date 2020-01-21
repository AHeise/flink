plugins {
    scala
}

dependencies {
    api(project(":flink-connectors:flink-hadoop-compatibility"))
    api(project(":flink-core"))

    implementation(project(":flink-java"))
    implementation(Libs.hcatalog_core version "0.12.0")
    implementation(Libs.scala_library)
    implementation(Libs.flink_shaded_hadoop_2)
}

description = "flink-hcatalog"
