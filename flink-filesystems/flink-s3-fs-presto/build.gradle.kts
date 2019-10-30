dependencies {
    shade(project(":flink-core"))
    shade(project(":flink-filesystems:flink-s3-fs-base"))
    shade(Libs.presto_hive)
    shade(Libs.hadoop_apache2)
    shade(project(":flink-filesystems:flink-hadoop-fs"))

    testImplementation(project(":flink-core", configuration = "testArtifacts"))
    testImplementation(project(":flink-filesystems:flink-hadoop-fs", configuration = "testArtifacts"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-s3-fs-presto"

tasks.withType<ShadowJar> {
    exclude("META-INF/maven/org.weakref/**")
    exclude("META-INF/maven/org.hdrhistogram/**")
    exclude("META-INF/maven/joda-time/**")
    exclude("META-INF/maven/io.airlift/**")
    exclude("META-INF/maven/com*/**")
    exclude("META-INF/maven/org.apache.flink/force-shading/**")
    exclude("META-INF/LICENSE.txt")

    //  relocate the references to Hadoop to match the pre-shaded hadoop artifact
    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")
    //  relocate the AWS dependencies
    relocate("com.amazon", "org.apache.flink.fs.s3base.shaded.com.amazon")
    //  relocate S3 presto and dependencies
    relocate("com.facebook", "org.apache.flink.fs.s3presto.shaded.com.facebook")
    relocate("com.fasterxml", "org.apache.flink.fs.s3presto.shaded.com.fasterxml")
    relocate("io.airlift", "org.apache.flink.fs.s3presto.shaded.io.airlift")
    relocate("javax.xml.bind", "org.apache.flink.fs.s3presto.shaded.javax.xml.bind")
    relocate("org.HdrHistogram", "org.apache.flink.fs.s3presto.shaded.org.HdrHistogram")
    relocate("org.joda", "org.apache.flink.fs.s3presto.shaded.org.joda")
    relocate("org.weakref", "org.apache.flink.fs.s3presto.shaded.org.weakref")
    relocate("org.openjdk", "org.apache.flink.fs.s3presto.shaded.org.openjdk")
    relocate("com.google", "org.apache.flink.fs.s3presto.shaded.com.google")

    //  shade Flink's Hadoop FS utility classes
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.s3presto.common")
}