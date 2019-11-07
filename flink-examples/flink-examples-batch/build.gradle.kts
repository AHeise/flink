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
    implementation(Libs.slf4j_api)

    testImplementation(Libs.slf4j_log4j12)
}

description = "flink-examples-batch"

flinkJointScalaJavaCompilation()