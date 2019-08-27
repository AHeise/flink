dependencies {
    implementation(project(":flink-core"))
    testImplementation(project(":flink-java"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    compileOnly(project(":flink-java"))
    compileOnly(project(":flink-scala"))
    compileOnly(Libs.flink_shaded_hadoop_2)
}

description = "flink-hadoop-compatibility"
