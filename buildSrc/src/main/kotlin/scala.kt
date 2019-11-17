import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ComponentArtifactIdentifier
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.ScalaSourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import kotlin.apply as kotlinApply

import org.gradle.kotlin.dsl.*

import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.plugins.scala.ScalaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.scala.ScalaCompile
import org.gradle.kotlin.dsl.support.delegates.ProjectDelegate

/**
 * Bread-first tests if there is scala-library in classpath and caches the result.
 */
fun Project.flinkMainDependsOnScala(): Boolean =
    extra.properties.getOrPut("flinkMainDependsOnScala") {
        configurations["compileClasspath"].flinkDependsOnScala()
    } as Boolean

fun Project.flinkTestDependsOnScala(): Boolean =
    extra.properties.getOrPut("flinkTestDependsOnScala") {
        configurations["testCompileClasspath"].flinkDependsOnScala()
    } as Boolean

private fun Configuration.flinkDependsOnScala(): Boolean =
    allDependencies.any {
        it.name == "scala-library" || it.name.endsWith(Versions.baseScala)
    } ||
    allDependencies.any {
        flinkDependsOnScala(it)
    }

private fun flinkDependsOnScala(dependency: Dependency): Boolean =
    if (dependency is ProjectDependency) {
        when (dependency.targetConfiguration) {
            "test" -> dependency.dependencyProject.flinkTestDependsOnScala()
            else -> dependency.dependencyProject.flinkMainDependsOnScala()
        }
    } else false

fun Project.flinkSetupScalaProjects() {



    subprojects {
        plugins.withType<ScalaPlugin> {
            // no need to check classpath if we know that the scala plugin has been added
            extra.properties["flinkMainDependsOnScala"] = true
            extra.properties["flinkTestDependsOnScala"] = true
            flinkJointScalaJavaCompilation()

            val scalaCompilerPlugin by configurations.creating

            configure<ScalaPluginExtension> {
                zincVersion.set("1.3.1")
            }

            dependencies {
                scalaCompilerPlugin("com.typesafe.genjavadoc:genjavadoc-plugin_${Versions.org_scala_lang}:0.13")
            }
        }

//        configurations.forEach {
//            it.dependencies.whenObjectAdded {
//                println("$project $it $this")
//            }
//        }

//        if (project.flinkMainDependsOnScala()) {
//            tasks.named<ShadowJar>("shadowJar") {
//                archiveBaseName.set("${archiveBaseName.get()}_${Versions.baseScala}")
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
//            archiveBaseName.set("${archiveBaseName.get()}_${Versions.baseScala}")
//        }
    }

//    flinkAddScalaVersionToArtifactsForScalaProjects()
    flinkJointJavadocForScalaProjects()
}

fun Project.flinkAddScalaVersionToArtifactsForScalaProjects() {
    gradle.taskGraph.whenReady {
        // add scala version to all artifacts when scala is a dependency (even without scala plugin)
        allTasks.filterIsInstance(ShadowJar::class.java).forEach { jar ->
            jar.kotlinApply {
                val isTestJar = name.startsWith("test")
                if (!isTestJar && project.flinkMainDependsOnScala()) {
                    archiveBaseName.set("${archiveBaseName.get()}_${Versions.baseScala}")

                    project.configure<PublishingExtension> {
                        publications.named<MavenPublication>("main") {
                            // sync artifact id if we added scala version
                            artifactId = archiveBaseName.get()
                        }
                    }

                    project.configurations["archives"].kotlinApply {
                        artifacts.remove(artifacts.find { it.toString().contains("jar") })
                    }
                    project.artifacts.add(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME, jar)
                } else if (isTestJar && project.flinkTestDependsOnScala()) {
                    archiveBaseName.set("${archiveBaseName.get()}_${Versions.baseScala}")
                }
            }
        }
    }
}

/**
 * Configures the current project to compile Scala and Java together instead of one after the other.
 */
fun Project.flinkJointScalaJavaCompilation() {
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
fun Project.flinkCompileScalaFirst() {
    // TODO this doesn't work yet
//    val compileJava by tasks.existing(JavaCompile::class)
//    val compileScala by tasks.existing(ScalaCompile::class)
//    compileJava {
//        dependsOn(compileScala)
//    }
//    compileScala {
//        dependsOn -= compileJava
//    }
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