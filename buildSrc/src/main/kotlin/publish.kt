import org.gradle.api.Project
import org.gradle.api.Task
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
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.javadoc.Javadoc

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ApacheNoticeResourceTransformer
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.component.ComponentWithVariants
import org.gradle.api.plugins.internal.JavaConfigurationVariantMapping
import org.gradle.api.tasks.scala.ScalaDoc

/**
 * Some "constants" as extension properties.
 */
val Project.TEST_JAR: String
    get() = "testJar"

val Project.TEST_ARTIFACTS: String
    get() = "testArtifacts"

/**
 * Configures the current project to provide a test jar under the
 * "testArtifacts" configuration.
 */
fun Project.flinkCreateTestJar() {
    configurations.create(TEST_ARTIFACTS) {
        extendsFrom(configurations["testRuntime"])
        extendsFrom(configurations["testApi"])
        extendsFrom(configurations["api"])
    }

    val testJar by tasks.register<Jar>(TEST_JAR) {
        archiveClassifier.set("tests")
        val testSourceSet = project.the<SourceSetContainer>()["test"]
        from(testSourceSet.output)
    }

    artifacts {
        add(TEST_ARTIFACTS, testJar)
    }

    configure<PublishingExtension> {
        publications {
            named("main", MavenPublication::class) {
                val additionalTestDependenies = configurations["testRuntimeClasspath"].dependencies - configurations["runtimeClasspath"].dependencies
                if(additionalTestDependenies.isNotEmpty()) {
                    pom.withXml {
                        asNode().appendNode("dependencies").let { depNode ->
                            additionalTestDependenies.forEach {
                                depNode.appendNode("dependency").apply {
                                    appendNode("groupId", it.group)
                                    appendNode("artifactId", it.name)
                                    appendNode("version", it.version)
                                    appendNode("scope", "test")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Project.flinkSetupPublising() {
    apply(plugin = "maven-publish")

    // build jar containing all (main) source files
    val sourceJar by tasks.registering(Jar::class) {
        dependsOn(tasks["classes"])
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
    }

    val javadoc by tasks.existing(Javadoc::class) {
        isFailOnError = false
    }
    // the javadoc jar for this module
    val javadocJar by tasks.registering(Jar::class) {
        dependsOn(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.get().destinationDir)
    }

    val shadowJar = flinkSetupShading()

    // disable regular jar task, we use only output of shadow
    tasks.named<org.gradle.api.tasks.bundling.Jar>("jar").configure {
        enabled = false
    }

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

                // replace the non-shaded jar and add javadoc and sources
                setArtifacts(listOf(shadowJar, javadocJar, sourceJar).map { LazyPublishArtifact(it) })
            }
        }
    }
}

fun Project.flinkSetupShading(): TaskProvider<ShadowJar> {
    apply(plugin = "com.github.johnrengelman.shadow")

    val shadowJar by tasks.existing(ShadowJar::class) {
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
    }

    // add shadow jar as requirement for full build
    tasks.named("build").configure {
        dependsOn(tasks.withType<ShadowJar>())
    }

    return shadowJar
}
