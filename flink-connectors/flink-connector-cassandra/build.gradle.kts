dependencies {
    api(Libs.cassandra_driver_core)

    implementation(project(":flink-streaming-scala"))
    implementation(project(":flink-scala"))
    implementation(project(":flink-table:flink-table-api-java-bridge"))
    implementation(project(":flink-table:flink-table-planner"))
    implementation(Libs.cassandra_driver_mapping)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.jsr305)

    testImplementation(project(":flink-streaming-java", configuration = TEST_JAR))
    testImplementation(project(":flink-tests"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    testImplementation(Libs.cassandra_all)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_module_junit4)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-connector-cassandra"

tasks.withType<ShadowJar> {

    relocate("com.google", "org.apache.flink.cassandra.shaded.com.google") {
        exclude("com.google.protobuf.**")
        exclude("com.google.inject.**")
    }
    //  Relocate to datastax' package where it expects shaded netty versions; see https://issues.apache.org/jira/browse/FLINK-8295
    relocate("io.netty", "com.datastax.shaded.netty")
}