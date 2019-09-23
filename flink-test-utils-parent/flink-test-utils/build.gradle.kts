import org.gradle.api.plugins.internal.DefaultAdhocSoftwareComponent
import org.gradle.api.plugins.internal.JavaConfigurationVariantMapping

plugins {
    id("com.github.johnrengelman.shadow")
    id("com.github.jk1.dependency-license-report")
}

dependencies {
    api(project(":flink-test-utils-parent:flink-test-utils-junit"))
    api(project(":flink-clients"))

    implementation(project(":flink-runtime"))
    implementation(project(":flink-optimizer"))
    implementation(project(":flink-runtime", configuration = "testArtifacts"))
    implementation(project(":flink-streaming-java"))

    shadow(Libs.flink_shaded_netty)
    implementation(Libs.curator_test)
    implementation(Libs.hadoop_minikdc)
    implementation(Libs.scala_library)
}

description = "flink-test-utils"

licenseReport {
    renderers = arrayOf<com.github.jk1.license.render.ReportRenderer>(com.github.jk1.license.render.TextReportRenderer("report.txt"))
//    filters = arrayOf<com.github.jk1.license.filter.DependencyFilter>(com.github.jk1.license.filter.LicenseBundleNormalizer())
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>().configureEach {
    // TODO: gradle: check if this is actually helping at all; it's not a direct dependency, so the shaded stuff is only partially used
    relocate("org.jboss.netty", "org.apache.flink.shaded.testutils.org.jboss.netty")
    exclude("META-INF/maven/io.netty/**")
    dependencies {
        include(dependency("io.netty:netty"))
    }
}