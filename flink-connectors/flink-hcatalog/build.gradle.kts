plugins {
    scala
}

dependencies {
    api(project(":flink-connectors:flink-hadoop-compatibility"))
    api(project(":flink-core"))

    implementation(project(":flink-java"))
    implementation(Libs.hcatalog_core)
    implementation(Libs.scala_library)
    implementation(Libs.flink_shaded_hadoop_2)
}

description = "flink-hcatalog"
