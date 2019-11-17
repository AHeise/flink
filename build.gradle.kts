import com.gradle.scan.plugin.BuildScanExtension

plugins {
    id("de.fayard.buildSrcVersions") version "0.4.2"
    id("org.nosphere.apache.rat") version "0.5.2"
}

allprojects {
    group = "org.apache.flink"
    version = "1.10-SNAPSHOT"
}

if (!gradle.startParameter.isOffline) {
    configure<BuildScanExtension> {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

subprojects {
    if(project.subprojects.isNotEmpty()) {
        return@subprojects
    }
    apply(plugin = "java-library")

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
    }

    tasks.withType<Test>().configureEach {
        useJUnit()
        systemProperty("log4j.configuration", "log4j-test.properties")
//        maxParallelForks = 24

        jvmArgs("-Xms256m", "-Xmx2048m", "-XX:+UseG1GC")
        include("**/*Test.*")
    }

    flinkSetupPublishing()
}

flinkSetupScalaProjects()

tasks.rat {
    file("$rootDir/.gitignore").forEachLine { exclude(it) }
    file("$rootDir/tools/rat.excludes").useLines { lines ->
        lines.filterNot { it.startsWith("#") || it.isEmpty() }
                .forEach { exclude(it) }
    }
}