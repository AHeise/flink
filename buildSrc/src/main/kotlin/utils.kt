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

//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
//import com.github.jengelman.gradle.plugins.shadow.transformers.*
//import org.gradle.api.file.FileTreeElement
//import org.gradle.api.tasks.util.PatternSet
//import org.apache.tools.zip.ZipOutputStream

/**
 * Configures the current project to provide a test jar under the
 * "testArtifacts" configuration.
 */
fun Project.flinkCreateTestJar() {
    val testArtifacts by configurations.creating {
        extendsFrom(configurations["testRuntime"])
        extendsFrom(configurations["testApi"])
        extendsFrom(configurations["api"])
    }

    val testJar by tasks.register<Jar>("testJar") {
        archiveClassifier.set("tests")
        val testSourceSet = project.the<SourceSetContainer>()["test"]
        from(testSourceSet.output)
    }

    artifacts {
        add("testArtifacts", testJar)
    }
}

fun DependencyHandler.`testApi`(dependencyNotation: Any): Dependency? =
    add("testApi", dependencyNotation)

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
 * Ensures all artifacts of the dependency/group to be of the specific version
 */
fun Project.flinkForceDependencyVersion(group: String? = null, name: String? = null, version: Any?) {
    configurations.all {
        resolutionStrategy.eachDependency {
            if ((group == null || target.group == group) && (name == null || target.name == name)) {
                useVersion(version.toString())
            }
        }
    }
}

/**
 * Excludes all matching modules from all configurations.
 */
fun Project.flinkExclude(group: String? = null, name: String? = null) {
    configurations.all {
        exclude(group = group, module = name)
    }
}
//
//inline fun ShadowJar.flinkInclude(spec: Spec<in ResolvedDependency>, noinline configuration: PatternFilterable.() -> Unit) {
//    dependencies {
//        include(spec)
//    }
//    val patternSpec = PatternSet()
//    configuration(patternSpec)
//    transform(FilteringResourceTransformer(patternSpec.getAsSpec()))
//}
//
//data class FilteringResourceTransformer(val spec: Spec<FileTreeElement>) : Transformer {
//    override fun canTransformResource(element: FileTreeElement): Boolean {
////        def path = element.relativePath.pathString
////                if (path.endsWith(resource)) {
////                    return true
////                }
//
//        return false
//    }
//
//    override fun transform(context: TransformerContext): Unit {
//        // no op
//    }
//
//    override fun hasTransformedResource(): Boolean {
//        return false
//    }
//
//    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean): Unit {
//        // no op
//    }
//}

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

