import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar

import org.gradle.kotlin.dsl.*

import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.javadoc.Javadoc

import com.github.jengelman.gradle.plugins.shadow.transformers.ApacheNoticeResourceTransformer
import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import java.net.URLClassLoader

// use typealias to keep customized shading options shorter
typealias ShadowJar = com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

const val TEST_JAR = "testJar"

private const val SHADE = "shade"

const val PUBLISH = "publish"

private const val TEST_SHADE = "testShade"

val CLASSIFIER_ATTRIBUTE = Attribute.of("classifier", String::class.java)

fun DependencyHandler.testShade(dependencyNotation: Any): Dependency? =
        add(TEST_SHADE, dependencyNotation)

fun DependencyHandler.shade(dependencyNotation: Any): Dependency? =
        add(SHADE, dependencyNotation)
/**
 * Configures the current project to provide a test jar under the
 * "testArtifacts" configuration.
 */
fun Project.flinkCreateTestJar(mainClass: String? = null, artifactName: String? = null, configuration: Action<ShadowJar>? = null) {
    try {
        configurations.register(TEST_JAR) {
            extendsFrom(configurations["testRuntime"])
            extendsFrom(configurations["testApi"])
            extendsFrom(configurations["api"])

            attributes {
                attribute(CLASSIFIER_ATTRIBUTE, "test")
            }
        }
    } catch (exception: InvalidUserDataException) {
        // there is currently no lazy way to check if a configuration already exists
    }

    val testJar by tasks.register<ShadowJar>(artifactName ?: TEST_JAR) {
        if (mainClass != null) {
            manifest {
                attributes(mapOf("Main-Class" to mainClass))
            }
        }

        if (artifactName != null) {
            archiveBaseName.set(artifactName)
        } else {
            archiveClassifier.set("tests")
        }
        val testSourceSet = project.the<SourceSetContainer>()["test"]
        from(testSourceSet.output)

        configuration?.execute(this)
    }

    artifacts {
        add(TEST_JAR, testJar)
    }
}

fun Project.flinkSetMainClass(mainClass: String) {
    apply(plugin = "application")

    configure<JavaApplication> {
        mainClassName = mainClass
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveBaseName.set(mainClass.substringAfterLast("."))

        if (logger.isDebugEnabled) {
            doLast {
                verifyClassExists(outputs.files, mainClass)
            }
        }
    }
}

private fun Task.verifyClassExists(files: FileCollection, mainClass: String) {
    class MainChecker : URLClassLoader(files.map { it.toURI().toURL() }.toTypedArray()) {
        fun hasClass(name: String?): Boolean = try {
            super.findClass(name)
            true
        } catch (e: Exception) {
            false
        }
    }

    if (!MainChecker().hasClass(mainClass)) {
        throw GradleException("Unknown class $mainClass for project $project")
    }
}


fun Project.flinkSetupPublishing() {
    apply(plugin = "maven-publish")

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }
//    // build jar containing all (main) source files
//    val sourceJar by tasks.registering(Jar::class) {
//        dependsOn(tasks["classes"])
//        archiveClassifier.set("sources")
//        from(project.the<SourceSetContainer>()["main"].allSource)
//    }

//    val javadoc by tasks.existing(Javadoc::class) {
//        isFailOnError = false
//    }
//    // the javadoc jar for this module
//    val javadocJar by tasks.registering(Jar::class) {
//        dependsOn(javadoc)
//        archiveClassifier.set("javadoc")
//        from(javadoc.get().destinationDir)
//    }
    tasks.named<Javadoc>("javadoc").configure {
        isFailOnError = false
    }

    flinkSetupShading()
    val actualJar = getMainJar()

    // tweak ALL jars (shaded, sources, javadoc)
    tasks.withType<org.gradle.api.tasks.bundling.Jar>().configureEach {
        // force reproducible builds; https://docs.gradle.org/5.6/userguide/working_with_files.html#sec:reproducible_archives
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    configure<PublishingExtension> {
        publications {
            register<MavenPublication>("main") {
                from(components["java"])


//                (components["java"] as DefaultAdhocSoftwareComponent).
//                setArtifacts(listOf(actualJar, javadocJar).map { LazyPublishArtifact(it) })
//                pom.withXml {
//                    val root = asNode()
//                    root.appendNode("dependencies").apply {
//
//                        addDependencies(null, configurations[PUBLISH].allDependencies)
//
//                        val additionalTestDependenies = configurations["testRuntimeClasspath"].allDependencies -
//                                configurations["runtimeClasspath"].allDependencies
//                        addDependencies("test", additionalTestDependenies)
//                    }
//                }
            }
        }
    }

    tasks.withType<GenerateMavenPom> {
        dependsOn(actualJar)
    }
}

private fun Project.getMainJar(): Provider<Jar> {
    val jar = tasks.named<Jar>("jar")
    val shadowJar = tasks.named<Jar>("shadowJar")

    // only one of jar or shadowJar will produce an output; also add javadoc and sources
    return providers.provider {
        if (!project.extra.has("mainJar")) {
            project.extra["mainJar"] = if (shadowJar.get().shouldRun) shadowJar.get() else jar.get()
        }
        project.extra["mainJar"] as Jar
    }
}

fun Project.flinkSetupShading(): TaskProvider<ShadowJar> {
    apply(plugin = "com.github.johnrengelman.shadow")

    configurations.register(SHADE) {
        configurations["implementation"].extendsFrom(this)
    }

    configurations.register(PUBLISH) {
    }

    val shadowJar by tasks.existing(ShadowJar::class)

    configurations.register(TEST_SHADE) {
        extendsFrom(configurations["testRuntime"])
        extendsFrom(configurations["testApi"])
        extendsFrom(configurations["api"])

        attributes {
            attribute(CLASSIFIER_ATTRIBUTE, "test")
        }
    }

    tasks.named<ShadowJar>("shadowJar") {
        onlyIf {
            // do we actually have anything to shade?
            project.configurations[SHADE].dependencies.isNotEmpty() || includes.isNotEmpty()
        }
        // create ad-hoc configuration containing all jars that should be shaded
        // we are using a configuration as that eases the exclusion of transitive dependencies
        doFirst {
            // do we actually have anything to shade?
            val shadedNoDist = this@flinkSetupShading.configurations.create("shadeWithoutFlinkDist")
            this@flinkSetupShading.dependencies {
                shadedNoDist(project.configurations[SHADE] -
                    rootProject.project(":flink-dist").configurations["runtimeClasspath"])
                PUBLISH(project.configurations["runtimeClasspath"] - shadedNoDist)
            }
            configurations = listOf(shadedNoDist)
        }

        // remove 'all' classifier, we want to replace the original jar by the shaded version
        archiveClassifier.set(null as String?)
        // publish still uses old classifier
        classifier = null

        // transformations
        mergeServiceFiles()
        transform(ApacheNoticeResourceTransformer().apply {
            projectName = "Apache Flink"
        })

        // global excludes
        exclude("log4j.properties", "log4j-test.properties")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        // exclude( com.google.code.findbugs:jsr305 )
    }

    // disable regular jar task iff shadowJar produces output
    tasks.named<org.gradle.api.tasks.bundling.Jar>("jar").configure {
        onlyIf { !shadowJar.get().shouldRun }
        dependsOn(shadowJar)
        doFirst {
            this@flinkSetupShading.dependencies {
                PUBLISH(configurations["runtimeClasspath"])
            }
        }
    }

    val mainJar = getMainJar()

    configurations["archives"].apply {
        artifacts.remove(artifacts.find { it.toString().contains("jar") })
    }

    val mainArtifact = LazyPublishArtifact(mainJar)
    artifacts.add(JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME, mainArtifact)
    artifacts.add(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME, mainArtifact)
    artifacts.add(Dependency.ARCHIVES_CONFIGURATION, mainArtifact)

    // add shadow jar as requirement for full build
    tasks.named("build").configure {
        dependsOn(mainJar)
    }

    return shadowJar
}