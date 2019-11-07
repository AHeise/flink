dependencies {
    implementation(project(":flink-table:flink-table-common"))
    implementation(project(":flink-table:flink-table-api-java"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-libraries:flink-cep"))
    implementation(Libs.janino)
    implementation(Libs.avatica_core)
    implementation(Libs.lz4_java)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_math3)
    implementation(Libs.scala_library)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-core", configuration = TEST_JAR))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
}

description = "flink-table-runtime-blink"

flinkCreateTestJar()

tasks.withType<ShadowJar> {
    //  Relocate org.lz4:lz4-java
    relocate("net.jpountz", "org.apache.flink.table.shaded.net.jpountz")
}