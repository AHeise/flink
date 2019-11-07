dependencies {
    compileOnly(project(":flink-annotations"))

    implementation(project(":flink-core"))
    implementation(project(":flink-filesystems:flink-hadoop-fs")) {
        exclude("org.apache.flink", "flink-shaded-hadoop-2")
    }
    implementation(Libs.hadoop_openstack)

    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-swift-fs-hadoop"


tasks.withType<ShadowJar> {
    exclude("log4j.properties")
    exclude("mime.types")
    exclude("properties.dtd")
    exclude("PropertyList-1.0.dtd")
    exclude("models/**")
    exclude("mozilla/**")
    exclude("META-INF/maven/com*/**")
    exclude("META-INF/maven/net*/**")
    exclude("META-INF/maven/software*/**")
    exclude("META-INF/maven/joda*/**")
    exclude("META-INF/maven/org.mortbay.jetty/**")
    exclude("META-INF/maven/org.apache.h*/**")
    exclude("META-INF/maven/org.apache.commons/**")
    exclude("META-INF/maven/org.apache.flink/flink-hadoop-fs/**")
    exclude("META-INF/maven/org.apache.flink/force-shading/**")
    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/ASL2.0")
    exclude("META-INF/README.txt")
    // -we use our own "shaded" core -default.xml: core-default-shaded.xml
    exclude("core-default.xml")
    // we only add a core - site.xml with unshaded classnames for the unit tests-- >
    exclude("core-site.xml")

    relocate("com.fasterxml", "org.apache.flink.fs.openstackhadoop.shaded.com.fasterxml")
    relocate("com.google", "org.apache.flink.fs.openstackhadoop.shaded.com.google") {
        exclude("com.google.code.findbugs.**")
    }
    relocate("com.nimbusds", "org.apache.flink.fs.openstackhadoop.shaded.com.nimbusds")
    relocate("com.squareup", "org.apache.flink.fs.openstackhadoop.shaded.com.squareup")
    relocate("net.jcip", "org.apache.flink.fs.openstackhadoop.shaded.net.jcip")
    relocate("net.minidev", "org.apache.flink.fs.openstackhadoop.shaded.net.minidev")

    //  relocate everything from the flink-hadoop-fs project
    relocate("org.apache.flink.runtime.fs.hdfs", "org.apache.flink.fs.openstackhadoop.shaded.org.apache.flink.runtime.fs.hdfs")
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.openstackhadoop.shaded.org.apache.flink.runtime.util") {
        include("org.apache.flink.runtime.util.**Hadoop*")
    }

    relocate("org.apache", "org.apache.flink.fs.openstackhadoop.shaded.org.apache") {
        //  keep all other classes of flink as they are (exceptions above)
        exclude("org.apache.flink.**")
        exclude("org.apache.log4j.**") // provided
    }

    relocate("org.codehaus", "org.apache.flink.fs.openstackhadoop.shaded.org.codehaus")
    relocate("org.joda", "org.apache.flink.fs.openstackhadoop.shaded.org.joda")
    relocate("org.mortbay", "org.apache.flink.fs.openstackhadoop.shaded.org.mortbay")
    relocate("org.tukaani", "org.apache.flink.fs.openstackhadoop.shaded.org.tukaani")
    relocate("org.znerd", "org.apache.flink.fs.openstackhadoop.shaded.org.znerd")
    relocate("okio", "org.apache.flink.fs.openstackhadoop.shaded.okio")
}