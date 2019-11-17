description = "flink-runtime"

plugins {
    scala
}

dependencies {
    api(project(":flink-core"))
    api(project(":flink-java"))
    api(project(":flink-metrics:flink-metrics-core"))

    implementation(project(":flink-queryable-state:flink-queryable-state-client-java"))
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(project(":flink-shaded-curator"))
    implementation(Libs.commons_io)
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.flink_shaded_guava)
    implementation(Libs.flink_shaded_asm)
    implementation(Libs.flink_shaded_jackson)
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_cli)
    implementation(Libs.javassist)
    implementation(Libs.scala_library)
    implementation(Libs.akka_actor_2_11)
    implementation(Libs.akka_stream_2_11)
    implementation(Libs.akka_protobuf_2_11)
    implementation(Libs.akka_slf4j_2_11)
    implementation(Libs.grizzled_slf4j_2_11)
    implementation(Libs.scopt_2_11)
    implementation(Libs.snappy_java)
    implementation(Libs.chill_2_11)
    implementation(Libs.oshi_core)
    implementation(Libs.jsr305)

    shade(Libs.flink_shaded_hadoop_2)
    shade(Libs.akka_remote_2_11)
    shade(Libs.zookeeper)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(path = ":flink-metrics:flink-metrics-core", configuration = TEST_JAR))
    testImplementation(project(path = ":flink-core", configuration = TEST_JAR))
    testImplementation(Libs.flink_shaded_netty_tcnative_dynamic)
    testImplementation(Libs.curator_test)
    testImplementation(Libs.scalatest_2_11)
    testImplementation(Libs.okhttp)
    testImplementation(Libs.akka_testkit_2_11)
    testImplementation(Libs.reflections)
    testImplementation(Libs.jcip_annotations)
    testImplementation(Libs.hamcrest_all)
    testImplementation(Libs.mockito_core)
    testImplementation(Libs.powermock_api_mockito2)
    testImplementation(Libs.powermock_module_junit4)
}

tasks.withType<Test> {
    maxHeapSize = "1024m"
}

//flinkJointScalaJavaCompilation()
flinkCreateTestJar()

tasks.withType<ShadowJar> {
    // io.netty:netty
    // Only some of these licenses actually apply to the JAR and have been manually placed in this module's resources directory.
    exclude("META-INF/license")
    // Only parts of NOTICE file actually apply to the netty JAR and have been manually copied into this modules's NOTICE file.
    exclude("META-INF/NOTICE.txt")
    // *
    exclude("META-INF/maven/io.netty/**")
    exclude("META-INF/maven/org.uncommons.maths/**")

    relocate("org.jboss.netty", "org.apache.flink.shaded.akka.org.jboss.netty")
    relocate("org.uncommons.math", "org.apache.flink.shaded.akka.org.uncommons.math")
    //  IMPORTANT: This must be kept in sync with flink-connector-kafka-0.8
    relocate("org.apache.curator", "org.apache.flink.shaded.curator.org.apache.curator") {
        // Do not relocate curator-test. This leads to problems for downstream users of runtime test classes that make
        // use of it as the relocated dependency is not included in the test-jar.-->
        exclude("org.apache.curator.test.**")
    }
    relocate("org.apache.zookeeper", "org.apache.flink.shaded.zookeeper.org.apache.zookeeper")
    //  jute is already shaded into the ZooKeeper jar
    relocate("org.apache.jute", "org.apache.flink.shaded.zookeeper.org.apache.zookeeper.jute")
}