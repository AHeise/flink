/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.jengelman.gradle.plugins.shadow.transformers.ApacheNoticeResourceTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import org.gradle.api.*
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTreeElement
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.util.PatternSet
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.*
import shadow.org.apache.tools.zip.ZipOutputStream
import java.net.URLClassLoader
import kotlin.reflect.KClass

const val TEST_JAR = "testJar"

val CLASSIFIER_ATTRIBUTE = Attribute.of("classifier", String::class.java)


/**
 * Configures the current project to provide a test jar under the
 * "testArtifacts" configuration.
 */
fun Project.flinkCreateTestJar(mainClass: String? = null, artifactName: String? = null, configuration: Action<ShadowJar>? = null) {
    try {
        configurations.register(TEST_JAR) {
            isVisible = false
            isCanBeConsumed = true
            isCanBeResolved = false
            extendsFrom(configurations[JavaPlugin.TEST_RUNTIME_CLASSPATH_CONFIGURATION_NAME])
//            extendsFrom(configurations[JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME])

            attributes {
                attribute(CLASSIFIER_ATTRIBUTE, "test")
            }
        }
    } catch (exception: InvalidUserDataException) {
        // there is currently no lazy way to check if a configuration already exists
    }

    val testJar = tasks.register<ShadowJar>(artifactName ?: TEST_JAR) {
        group = "build"

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
    tasks.named("jar") { dependsOn(testJar) }

    val testArtifact = LazyPublishArtifact(testJar)
    artifacts {
        add(TEST_JAR, testArtifact)
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
        options.encoding = "UTF-8"
        val opts = options as StandardJavadocDocletOptions
        // suppress warnings
        opts.addStringOption("Xdoclint:none", "-quiet")
    }

    flinkSetupShading()

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

//    tasks.withType<GenerateMavenPom> {
//        dependsOn(actualJar)
//    }
}
