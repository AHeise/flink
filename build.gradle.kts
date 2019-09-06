plugins {
    id("de.fayard.buildSrcVersions") version "0.4.2"
    id("com.gradle.build-scan") version "2.1"
    id("org.nosphere.apache.rat") version "0.5.2"
}

apply("$rootDir/gradle/dependencies.gradle")

allprojects {
    group = "org.apache.flink"
    version = "1.10-SNAPSHOT"
}

subprojects {
    apply(plugin = "java-library")

    configurations.register("testApi") {
        extendsFrom(configurations["api"])
        configurations["testImplementation"].extendsFrom(this)
        isTransitive = true
    }

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

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
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

    tasks.withType<Jar> {
        exclude("log4j.properties", "log4j-test.properties")
    }
    // force reproducible builds; https://docs.gradle.org/5.6/userguide/working_with_files.html#sec:reproducible_archives
    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>().configureEach {
        mergeServiceFiles()
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.ApacheNoticeResourceTransformer().apply {
            projectName = "Apache Flink"
        })
    }
}

tasks.rat {
    excludeFile.set(file("$rootDir/.gitignore"))
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}
