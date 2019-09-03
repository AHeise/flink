plugins {
    scala
}

dependencies {
    api(project(":flink-table:flink-table-api-java"))
    api(project(":flink-streaming-java"))
    api(project(":flink-table:flink-table-api-scala"))

    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-scala"))
    implementation(project(":flink-streaming-scala"))
}

description = "flink-table-api-scala-bridge"

flinkJointScalaJavaCompilation()
