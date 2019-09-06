plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    api(project(":flink-test-utils-parent:flink-test-utils-junit"))
    api(project(":flink-clients"))
    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(path = ":flink-runtime", configuration = "testArtifacts"))
    implementation(project(":flink-streaming-java"))
    implementation(Libs.flink_shaded_netty)
    implementation(Libs.curator_test)
    implementation(Libs.hadoop_minikdc)
    implementation(Libs.scala_library)
}

description = "flink-test-utils"


tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>().configureEach {
    relocate("org.jboss.netty", "org.apache.flink.shaded.testutils.org.jboss.netty")

    dependencies {
        include(dependency("io.netty:netty"))
            exclude("META-INF/maven/io.netty/**")
            // Only some of these licenses actually apply to the JAR and have been manually placed in this module's resources directory.
            exclude("META-INF/license")
            // Only parts of NOTICE file actually apply to the netty JAR and have been manually copied into this modules's NOTICE file.
            exclude("META-INF/NOTICE.txt")
    }
}