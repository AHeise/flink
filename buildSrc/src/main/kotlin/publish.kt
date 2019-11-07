import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar

import org.gradle.kotlin.dsl.*

import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.javadoc.Javadoc

import com.github.jengelman.gradle.plugins.shadow.transformers.ApacheNoticeResourceTransformer
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Provider
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import java.net.URLClassLoader

// use typealias to keep customized shading options shorter
typealias ShadowJar = com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

const val TEST_JAR = "testJar"

private const val SHADE = "shade"

const val SHADED = "shaded"

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

private fun Node.appendDependency(
        dependency: ModuleDependency,
        extension: String = "jar",
        classifier: String? = null) {
    appendNode("groupId", dependency.group)
    appendNode("artifactId", dependency.name)
    appendNode("version", dependency.version)
    appendNode("scope", "test")
    if (extension != "jar") {
        appendNode("packaging", extension)
    }
    if (classifier != null) {
        appendNode("classifier", classifier)
    }
}

fun Project.flinkSetupPublishing() {
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

                setArtifacts(listOf(actualJar, javadocJar, sourceJar).map { LazyPublishArtifact(it) })
            }
        }
    }

    tasks.withType<GenerateModuleMetadata> {
        dependsOn(actualJar)
    }
}

private fun Project.getMainJar(): Provider<Jar> {
    val jar = tasks.named<Jar>("jar")
    val shadowJar = tasks.named<Jar>("shadowJar")

    // only one of jar or shadowJar will produce an output; also add javadoc and sources
    return providers.provider {
        if (jar.get().shouldRun) jar.get() else shadowJar.get()
    }
}

fun Project.flinkSetupShading(): TaskProvider<ShadowJar> {
    apply(plugin = "com.github.johnrengelman.shadow")

    configurations.register(SHADE) {
        configurations["implementation"].extendsFrom(this)
    }

    configurations.register(SHADED) {
    }

    val shadowJar by tasks.existing(ShadowJar::class)

    // disable regular jar task iff shadowJar produces output
    tasks.named<org.gradle.api.tasks.bundling.Jar>("jar").configure {
        onlyIf { !shadowJar.get().shouldRun }
    }

    artifacts {
        add(SHADED, shadowJar)
    }

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
            val shadedDependencies = project.configurations[SHADE]
            !shadedDependencies.isEmpty || includes.isNotEmpty()
        }
        // create ad-hoc configuration containing all jars that should be shaded
        // we are using a configuration as that eases the exclusion of transitive dependencies
        doFirst {
            // do we actually have anything to shade?
            val shadedNoDist = this@flinkSetupShading.configurations.create("shadeWithoutFlinkDist")
            this@flinkSetupShading.dependencies {
                shadedNoDist(project.configurations[SHADE] -
                    rootProject.project(":flink-dist").configurations["runtimeClasspath"])
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

    // add shadow jar as requirement for full build
    tasks.named("build").configure {
        dependsOn(getMainJar())
    }

    return shadowJar
}