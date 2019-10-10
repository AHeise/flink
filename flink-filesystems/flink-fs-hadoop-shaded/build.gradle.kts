dependencies {
    implementation(Libs.hadoop_common)
}

description = "flink-filesystems :: flink-fs-hadoop-shaded"

flinkCreateTestJar()

tasks.withType<ShadowJar> {
    // org.apache.hadoop:hadoop-common
    exclude("org/apache/hadoop/conf/Configuration**")
    exclude("org/apache/hadoop/util/NativeCodeLoader**")
    exclude("org/apache/hadoop/util/VersionInfo**")
    exclude("core-default.xml")
    exclude("common-version-info.properties")
    exclude("org.apache.hadoop.application-classloader.properties")
    // *
    exclude("properties.dtd")
    exclude("PropertyList-1.0.dtd")
    exclude("META-INF/maven/**")
    exclude("META-INF/services/javax.xml.stream.*")
    exclude("META-INF/LICENSE.txt")

    // we shade only the parts that are internal to Hadoop and not used / exposed downstream
    relocate("com.google.re2j", "org.apache.flink.fs.shaded.hadoop3.com.google.re2j")
    relocate("org.apache.htrace", "org.apache.flink.fs.shaded.hadoop3.org.apache.htrace")
    relocate("com.fasterxml", "org.apache.flink.fs.shaded.hadoop3.com.fasterxml")
    relocate("org.codehaus", "org.apache.flink.fs.shaded.hadoop3.org.codehaus")
    relocate("com.ctc", "org.apache.flink.fs.shaded.hadoop3.com.ctc")
}