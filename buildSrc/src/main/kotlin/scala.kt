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

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.ScalaSourceSet
import org.gradle.api.tasks.SourceSetContainer
import kotlin.apply as kotlinApply

import org.gradle.kotlin.dsl.*

import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.plugins.scala.ScalaPluginExtension
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.scala.ScalaCompile

val Project.scalaMinorVersion
    get() = stringProperty("scala.binary.version")
/**
 * Bread-first tests if there is scala-library in classpath and caches the result.
 */
fun Project.flinkIsMainDependingOnScala(): Boolean =
    extra.getOrPut("flinkIsMainDependingOnScala") {
        flinkIsConfigurationDependingOnScala(configurations["compileClasspath"])
    }

fun Project.flinkIsTestDependingOnScala(): Boolean =
    extra.getOrPut("flinkIsTestDependingOnScala") {
        flinkIsConfigurationDependingOnScala(configurations["testCompileClasspath"])
    }

private fun Project.flinkIsConfigurationDependingOnScala(conf: Configuration): Boolean =
    conf.allDependencies.any {
        it.name == "scala-library" || it.name.endsWith(project.scalaMinorVersion)
    } ||
    conf.allDependencies.any {
        flinkIsDependencyDependingOnScala(it)
    }

private fun flinkIsDependencyDependingOnScala(dependency: Dependency): Boolean =
    if (dependency is ProjectDependency) {
        when (dependency.targetConfiguration) {
            "test" -> dependency.dependencyProject.flinkIsTestDependingOnScala()
            else -> dependency.dependencyProject.flinkIsMainDependingOnScala()
        }
    } else false

fun Project.flinkSetupScalaProjects() {
    subprojects {
        plugins.withType<ScalaPlugin> {
            // no need to check classpath if we know that the scala plugin has been added
            extra.properties["flinkMainDependsOnScala"] = true
            extra.properties["flinkTestDependsOnScala"] = true

//            val scalaCompilerPlugin by configurations.creating

            configure<ScalaPluginExtension> {
                zincVersion.set("1.3.5")
            }

//            dependencies {
//                scalaCompilerPlugin("com.typesafe.genjavadoc:genjavadoc-plugin_${stringProperty("scala.version")}:0.15")
//            }
        }

//        configurations.forEach {
//            it.dependencies.whenObjectAdded {
//                println("$project $it $this")
//            }
//        }

//        if (project.flinkMainDependsOnScala()) {
//            tasks.named<ShadowJar>("shadowJar") {
//                archiveBaseName.set("${archiveBaseName.get()}_${Versions.scalaMinorVersion}")
//            }
//            project.configure<PublishingExtension> {
//                publications.named<MavenPublication>("main") {
//                    // sync artifact id if we added scala version
//                    artifactId = archiveBaseName.get()
//                }
//            }
//
//            project.configurations["archives"].kotlinApply {
//                artifacts.remove(artifacts.find { it.toString().contains("jar") })
//            }
//            project.artifacts.add(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME, jar)
//        } else if (isTestJar && project.flinkTestDependsOnScala()) {
//            archiveBaseName.set("${archiveBaseName.get()}_${Versions.scalaMinorVersion}")
//        }
    }

    flinkAddScalaVersionToArtifactsForScalaProjects()
//    flinkJointJavadocForScalaProjects()
}

fun Project.flinkAddScalaVersionToArtifactsForScalaProjects() {
    gradle.addBuildListener(object : BuildListener {
        override fun settingsEvaluated(settings: Settings) {
        }

        override fun buildFinished(result: BuildResult) {
        }

        override fun projectsLoaded(gradle: Gradle) {
        }

        override fun buildStarted(gradle: Gradle) {
        }

        override fun projectsEvaluated(gradle: Gradle) {
            subprojects {
                tasks.withType<ShadowJar>().configureEach {
                    val isTestJar = name.startsWith("test")
                    if (!isTestJar && project.flinkIsMainDependingOnScala()) {
                        archiveBaseName.set("${archiveBaseName.get()}_${scalaMinorVersion}")
                    } else if (isTestJar && project.flinkIsTestDependingOnScala()) {
                        archiveBaseName.set("${archiveBaseName.get()}_${scalaMinorVersion}")
                    }
                }
            }
        }
    })
}

/**
 * Configures the current project to compile Scala and Java together instead of one after the other.
 */
fun Project.`flinkJointScalaJavaCompilation`() {
    val sourceSets = the<SourceSetContainer>()
    sourceSets {
        named("main") {
            withConvention(ScalaSourceSet::class) {
                scala {
                    setSrcDirs(listOf("src/main/scala", "src/main/java"))
                }
            }
            java {
                setSrcDirs(emptyList<String>())
            }
        }

        named("test") {
            withConvention(ScalaSourceSet::class) {
                scala {
                    setSrcDirs(listOf("src/test/scala", "src/test/java"))
                }
            }
            java {
                setSrcDirs(emptyList<String>())
            }
        }
    }
}

/**
 * Configures the current project to first compile Scala, then Java. The default order is the other
 * way round with the Gradle scala plugin.
 */
fun Project.flinkCompileScalaFirst(test: Boolean = false) {
    val sourceSets = the<SourceSetContainer>()
    val sourceSet = if(test) "test" else "main"
    val taskInfix = if(test) "Test" else ""
    tasks.named<ScalaCompile>("compile${taskInfix}Scala") {
        // Scala only needs the declared dependencies
        // and not the output of compileJava
        classpath = sourceSets[sourceSet].compileClasspath
    }
    tasks.named<JavaCompile>("compile${taskInfix}Java") {
        // Java also depends on the result of Scala compilation
        // which automatically makes it depend of compileJava
        sourceSets[sourceSet].withConvention(ScalaSourceSet::class) {
            classpath += files(scala.classesDirectory)
        }
    }
}


fun Project.flinkJointJavadocForScalaProjects() {
    gradle.taskGraph.whenReady {
        allTasks.filter { it.name == "javadoc" }.forEach { doc ->
            doc.project.kotlinApply {
                val scalaCompile = tasks.withType<ScalaCompile>()
                if(scalaCompile.isNotEmpty()) {
                    scalaCompile.configureEach {
                        scalaCompileOptions.additionalParameters = listOf(
                                "-Xplugin:" + configurations["scalaCompilerPlugin"].asPath,
                                "-P:genjavadoc:out=$buildDir/generated/java")
                    }

                    tasks.withType<Javadoc> {
                        dependsOn(tasks.withType<ScalaCompile>())
                        setSource(listOf(project.the<SourceSetContainer>()["main"].allJava, "$buildDir/generated/java"))
                    }
                }
            }
        }
    }
}