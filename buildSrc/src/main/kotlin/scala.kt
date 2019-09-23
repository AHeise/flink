import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.ScalaSourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.jvm.tasks.Jar

import org.gradle.kotlin.dsl.*

import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.scala.ScalaCompile

fun Project.flinkSetupScalaIfNeeded() {
    plugins.withType<ScalaPlugin> {
        flinkJointScalaJavaCompilation()
        flinkJointJavadoc()
    }

    // add scala version to all artifacts when scala is a dependency (even without scala plugin)
    // there is currently no way to listen when a specific dependency has been added
    // so we just wait until the whole build.gradle of a project has been evaluated
    afterEvaluate {
        println("Scala project? " + project.name)
        if (project.configurations["compileClasspath"].allDependencies.any { it.name == "scala-library" }) {
            println("Scala project!")
            tasks.withType<Jar>().configureEach {
                archiveBaseName.set("${archiveBaseName.get()}_${Versions.baseScala}")
            }
            configure<PublishingExtension> {
                publications.filterIsInstance<MavenPublication>().forEach { pub ->
                    // sync artifact id if we added scala version
                    pub.artifactId = (tasks["shadowJar"] as Jar).archiveBaseName.get()
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

fun Project.flinkJointJavadoc() {
    apply(plugin = "scala")

    val scalaCompilerPlugin by configurations.creating

    dependencies {
        scalaCompilerPlugin("com.typesafe.genjavadoc:genjavadoc-plugin_${Versions.org_scala_lang}:0.13")
    }

    tasks.withType<ScalaCompile> {
        scalaCompileOptions.additionalParameters = listOf(
                "-Xplugin:" + scalaCompilerPlugin.asPath,
                "-P:genjavadoc:out=$buildDir/generated/java")
    }

    tasks.withType<Javadoc> {
        dependsOn(tasks.named("compileScala"))
        setSource(listOf(project.the<SourceSetContainer>()["main"].allJava, "$buildDir/generated/java"))
    }
}