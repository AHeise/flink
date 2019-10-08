import org.gradle.api.Project
import org.gradle.api.tasks.ScalaSourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar

import org.gradle.kotlin.dsl.*

import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.scala.ScalaCompile

/**
 * Tests if there is scala-library in classpath and caches the result.
 */
fun Project.flinkMainDependsOnScala(): Boolean =
        extra.properties.getOrPut("flinkMainDependsOnScala") {
            configurations["compileClasspath"].allDependencies.any { it.name == "scala-library" }
        } as Boolean

fun Project.flinkTestDependsOnScala(): Boolean =
        extra.properties.getOrPut("flinkTestDependsOnScala") {
            configurations["testCompileClasspath"].allDependencies.any { it.name == "scala-library" }
        } as Boolean

fun Project.flinkSetupScalaIfNeeded() {
    plugins.withType<ScalaPlugin> {
        // no need to check classpath if we know that the scala plugin has been added
        extra.properties["flinkMainDependsOnScala"] = true
        extra.properties["flinkTestDependsOnScala"] = true
        flinkJointScalaJavaCompilation()
        flinkJointJavadoc()
    }

    // add scala version to all artifacts when scala is a dependency (even without scala plugin)
    tasks.withType<Jar>().configureEach {
        val isTestJar = name.startsWith("test")
        if(!isTestJar && flinkMainDependsOnScala()) {
            archiveBaseName.set("${archiveBaseName.get()}_${Versions.baseScala}")
        }
        else if(isTestJar && flinkTestDependsOnScala()) {
            archiveBaseName.set("${archiveBaseName.get()}_${Versions.baseScala}")
        }
    }

    configure<PublishingExtension> {
        if(flinkMainDependsOnScala()) {
            val main by publications.existing(MavenPublication::class) {
                // sync artifact id if we added scala version
                artifactId = (tasks["shadowJar"] as Jar).archiveBaseName.get()
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