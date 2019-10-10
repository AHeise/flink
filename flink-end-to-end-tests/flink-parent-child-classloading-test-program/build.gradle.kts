dependencies {
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-java"))
}

description = "flink-parent-child-classloading-test-program"

tasks.withType<ShadowJar> {
    archiveBaseName.set("ClassLoaderTestProgram")
}

flinkSetMainClass("org.apache.flink.streaming.tests.ClassLoaderTestProgram")