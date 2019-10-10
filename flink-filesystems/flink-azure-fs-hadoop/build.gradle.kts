dependencies {
    implementation(project(":flink-filesystems:flink-hadoop-fs"))
    implementation(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    implementation(Libs.hadoop_azure)
    implementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(Libs.azure)
    testImplementation(project(path = ":flink-core", configuration = "testArtifacts"))
    implementation(project(":flink-core"))
}

description = "flink-azure-fs-hadoop"

tasks.withType<ShadowJar> {
    exclude("properties.dtd")
    exclude("PropertyList-1.0.dtd")
    exclude("mozilla/**")
    exclude("META-INF/maven/**")
    exclude("META-INF/LICENSE.txt")

    //  relocate the references to Hadoop to match the shaded Hadoop config
    relocate("org.apache.hadoop", "org.apache.flink.fs.shaded.hadoop3.org.apache.hadoop")

    //  shade dependencies internally used by Hadoop and never exposed downstream
    relocate("org.apache.commons", "org.apache.flink.fs.shaded.hadoop3.org.apache.commons")

    //  relocate the Azure dependencies
    relocate("com.microsoft.azure", "org.apache.flink.fs.azure.shaded.com.microsoft.azure")

    //  shade dependencies internally used by Azure and never exposed downstream
    relocate("org.apache.httpcomponents", "org.apache.flink.fs.azure.shaded.org.apache.httpcomponents")
    relocate("commons-logging", "org.apache.flink.fs.azure.shaded.commons-logging")
    relocate("commons-codec", "org.apache.flink.fs.azure.shaded.commons-codec")
    relocate("com.fasterxml", "org.apache.flink.fs.azure.shaded.com.fasterxml")
    relocate("com.google", "org.apache.flink.fs.azure.shaded.com.google")
    relocate("org.eclipse", "org.apache.flink.fs.azure.shaded.org.eclipse")

    //  shade Flink's Hadoop FS adapter classes
    relocate("org.apache.flink.runtime.fs.hdfs", "org.apache.flink.fs.azure.common.hadoop")
    //  shade Flink's Hadoop FS utility classes
    relocate("org.apache.flink.runtime.util", "org.apache.flink.fs.azure.common")
}