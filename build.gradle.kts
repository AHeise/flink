import com.gradle.scan.plugin.BuildScanExtension
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact

plugins {
    id("de.fayard.buildSrcVersions") version "0.4.2"
    id("com.gradle.build-scan") version "2.1" apply false
    id("org.nosphere.apache.rat") version "0.5.2"
}

if (!gradle.startParameter.isOffline) {
    apply(plugin = "com.gradle.build-scan")

    configure<BuildScanExtension> {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

allprojects {
    group = "org.apache.flink"
    version = "1.10-SNAPSHOT"
}

subprojects {
    if(project.subprojects.isNotEmpty()) {
        return@subprojects
    }
    apply(plugin = "java-library")

    flinkSetupScalaIfNeeded()
    flinkRegisterTestApi()

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<ScalaCompile> {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
        maven(url = "https://packages.confluent.io/maven/")
    }

    dependencies {
        // TODO move these to only the modules that need them and remove them
        "implementation"(Libs.slf4j_api)
        "implementation"(Libs.jsr305)
        "testImplementation"(Libs.junit)
        "testImplementation"(Libs.mockito_core)
        "testImplementation"(Libs.powermock_module_junit4)
        "testImplementation"(Libs.powermock_api_mockito2)
        "testImplementation"(Libs.hamcrest_all)
        "testImplementation"(Libs.slf4j_log4j12)
        "testImplementation"(Libs.log4j)
    }

    tasks.withType<Test>().configureEach {
        useJUnit()
        systemProperty("log4j.configuration", "log4j-test.properties")
//        maxParallelForks = 24

        jvmArgs("-Xms256m", "-Xmx2048m", "-XX:+UseG1GC")
        include("**/*Test.*")
    }

    flinkSetupPublising()
}

tasks.rat {
    file("$rootDir/.gitignore").forEachLine { exclude(it) }
    file("$rootDir/tools/rat.excludes").useLines { lines ->
        lines.filterNot { it.startsWith("#") || it.isEmpty() }
                .forEach { exclude(it) }
    }
}
