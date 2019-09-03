plugins {
    id("scala")
}

dependencies {
    implementation(project(":flink-java"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-clients"))
    implementation(Libs.commons_io)
    implementation(Libs.commons_lang3)
    implementation(Libs.scala_library)
}

description = "flink-examples-batch"

flinkJointScalaJavaCompilation()