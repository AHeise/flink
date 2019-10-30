dependencies {
    implementation(project(":flink-core"))

    shade(project(":flink-filesystems:flink-fs-hadoop-shaded"))
    shade(project(":flink-filesystems:flink-hadoop-fs"))
    shade(Libs.aws_java_sdk_core)
    shade(Libs.aws_java_sdk_s3)
    shade(Libs.aws_java_sdk_kms)
    shade(Libs.aws_java_sdk_dynamodb)
    shade(Libs.hadoop_aws)
    shade(Libs.commons_io)

    testImplementation(project(":flink-core"))
    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
}

description = "flink-s3-fs-base"

tasks.withType<ShadowJar> {
    exclude(".gitkeep")
    exclude("mime.types")
    exclude("mozilla/**")
    exclude("META-INF/maven/**")
    exclude("META-INF/LICENSE.txt")

    //  shade dependencies internally used by Hadoop and never exposed downstream
    relocate("org.apache.commons", "org.apache.flink.fs.shaded.hadoop3.org.apache.commons")

    //  shade dependencies internally used by AWS and never exposed downstream
    relocate("software.amazon", "org.apache.flink.fs.s3base.shaded.software.amazon")
    relocate("org.joda", "org.apache.flink.fs.s3base.shaded.org.joda")
    relocate("org.apache.http", "org.apache.flink.fs.s3base.shaded.org.apache.http")
    relocate("com.fasterxml", "org.apache.flink.fs.s3base.shaded.com.fasterxml")
    relocate("com.google", "org.apache.flink.fs.s3base.shaded.com.google")

    //  shade Flink's Hadoop FS adapter classes
    relocate("org.apache.flink.runtime.fs.hdfs", "org.apache.flink.fs.s3.common.hadoop")
}