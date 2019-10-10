dependencies {
    api(project(":flink-connectors:flink-connector-elasticsearch-base"))

    implementation(Libs.transport)
    implementation(Libs.transport_netty3_client)
    implementation(Libs.log4j_to_slf4j)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils"))
    testImplementation(project(":flink-streaming-java"))
    testImplementation(project(":flink-connectors:flink-connector-elasticsearch-base", configuration = "testArtifacts"))
    testImplementation(Libs.log4j_core)
}

description = "flink-connector-elasticsearch5"

flinkForceDependencyVersion(name = "org.elasticsearch", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.client", version = project.property("elasticsearch.version"))
flinkForceDependencyVersion(group = "org.elasticsearch.plugin", version = project.property("elasticsearch.version"))

tasks.withType<ShadowJar> {
    // *
    exclude("log4j.properties")
    exclude("config/favicon.ico")
    exclude("mozilla/**")
    exclude("META-INF/maven/com*/**")
    exclude("META-INF/maven/io*/**")
    exclude("META-INF/maven/joda*/**")
    exclude("META-INF/maven/net*/**")
    exclude("META-INF/maven/org.an*/**")
    exclude("META-INF/maven/org.apache.h*/**")
    exclude("META-INF/maven/org.apache.commons/**")
    exclude("META-INF/maven/org.apache.flink/force-shading/**")
    exclude("META-INF/maven/org.apache.logging*/**")
    exclude("META-INF/maven/org.e*/**")
    exclude("META-INF/maven/org.h*/**")
    exclude("META-INF/maven/org.j*/**")
    exclude("META-INF/maven/org.y*/**")
    //  some dependencies bring their own LICENSE.txt which we don't need
    // *:*
    exclude("META-INF/LICENSE.txt")
    // io.netty:netty
    // Only some of these licenses actually apply to the JAR and have been manually
    // placed in this module's resources directory.
    exclude("META-INF/license")
    // Only parts of NOTICE file actually apply to the netty JAR and have been manually
    // copied into this modules's NOTICE file.
    exclude("META-INF/NOTICE.txt")


    relocate("com.carrotsearch", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.carrotsearch")
    relocate("com.fasterxml", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.fasterxml")
    relocate("com.github", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.github")
    relocate("com.sun", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.sun")
    relocate("com.tdunning", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.com.tdunning")
    relocate("io.netty", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.io.netty")
    relocate("org.apache", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.apache") {
        //  keep flink classes as they are (exceptions as above)
        exclude("org.apache.flink.**")
        exclude("org.apache.log4j.**")
    }
    relocate("org.HdrHistogram", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.HdrHistogram")
    relocate("org.jboss", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.jboss")
    relocate("org.joda", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.joda")
    relocate("org.tartarus", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.tartarus")
    relocate("org.yaml", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.org.yaml")
    relocate("joptsimple", "org.apache.flink.streaming.connectors.elasticsearch5.shaded.joptsimple")
}