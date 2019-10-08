import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar

import org.gradle.kotlin.dsl.*

import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.javadoc.Javadoc

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ApacheNoticeResourceTransformer
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.artifacts.*
import org.gradle.api.attributes.Attribute

/**
 * Some "constants" as extension properties.
 */
val TEST_JAR = "testJar"

val TEST_ARTIFACTS = "testArtifacts"

val CLASSIFIER_ATTRIBUTE = Attribute.of("classifier", String::class.java)
/**
 * Configures the current project to provide a test jar under the
 * "testArtifacts" configuration.
 */
fun Project.flinkCreateTestJar() {
    configurations.create(TEST_ARTIFACTS) {
        extendsFrom(configurations["testRuntime"])
        extendsFrom(configurations["testApi"])
        extendsFrom(configurations["api"])

        attributes {
            attribute(CLASSIFIER_ATTRIBUTE, "test")
        }
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
        publications.named("main", MavenPublication::class) {
            pom.withXml {
                val root = asNode()
                val dependencies = (root.get("dependencies") as NodeList?)?.get(0) as Node?
                (dependencies ?: root.appendNode("dependencies")).let { depNode ->
                    val additionalTestDependenies = configurations["testRuntimeClasspath"].allDependencies -
                            configurations["runtimeClasspath"].allDependencies

                    additionalTestDependenies.forEach { dependency ->
                        addTestDependency(depNode, dependency)
                    }
                }
            }
        }
    }
}

private fun Project.addTestDependency(depNode: Node, dependency: Dependency?) {
    depNode.appendNode("dependency").apply {
        when (dependency) {
            is ProjectDependency -> {
                val classifier = dependency.targetConfiguration?.let {
                    configurations[it].attributes.getAttribute(CLASSIFIER_ATTRIBUTE)
                }
                appendDependency(dependency, classifier = classifier)
            }
            is ExternalModuleDependency -> {
                if (dependency.artifacts.isEmpty()) {
                    appendDependency(dependency)
                } else {
                    for (artifact in dependency.artifacts) {
                        appendDependency(dependency, extension = artifact.extension, classifier = artifact.classifier)
                    }
                }
            }
        }
    }
}


private fun Node.appendDependency(dependency: ModuleDependency, extension: String? = null,
                                   classifier: String? = null) {
    appendNode("groupId", dependency.group)
    appendNode("artifactId", dependency.name)
    appendNode("version", dependency.version)
    appendNode("scope", "test")
    extension?.also { appendNode("packaging", it) }
    classifier?.also { appendNode("classifier", it) }
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
    }

    // add shadow jar as requirement for full build
    tasks.named("build").configure {
        dependsOn(tasks.withType<ShadowJar>())
    }

    return shadowJar
}