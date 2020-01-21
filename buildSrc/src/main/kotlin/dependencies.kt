import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.internal.artifacts.dsl.dependencies.ModuleFactoryHelper
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get

fun DependencyHandler.`testApi`(dependencyNotation: Any): Dependency? =
    add("testApi", dependencyNotation)

fun Project.flinkRegisterTestApi() {
    configurations.register("testApi") {
        extendsFrom(configurations["api"])
        configurations["testImplementation"].extendsFrom(this)
        isTransitive = true
    }
}

fun <T : ModuleDependency> T.exclude(dependency: Dependency): T =
    exclude(group = dependency.group, module = dependency.name)

infix fun ExternalDependency.version(version: String): ExternalDependency =
    DefaultExternalModuleDependency(group, name, version, targetConfiguration).also {
        it.artifacts = this.artifacts
    }

infix fun ExternalDependency.classifier(classifier: String): ExternalDependency =
    copy().also {
        ModuleFactoryHelper.addExplicitArtifactsIfDefined(it, null, classifier)
    }

/**
 * Excludes all matching modules from all configurations.
 */
fun Project.flinkExclude(group: String? = null, name: String? = null) {
    configurations.all {
        exclude(group = group, module = name)
    }
}

@Suppress("UNCHECKED_CAST")
fun Project.getAllProjectDependencies(): Set<ProjectDependency> =
    extra.getOrPut("allProjectDependencies") {
        configurations["runtimeElements"].allDependencies.filterIsInstance<ProjectDependency>().flatMap {
            setOf(it) + it.dependencyProject.getAllProjectDependencies()
        }.toSet()
    }


@Suppress("UNCHECKED_CAST")
fun Project.getDistProjectDependencies(configurationName: String): Set<ProjectDependency> =
    extra.getOrPut("${configurationName}DistProjectDependencies") {
        val distProjects = project.evaluationDependsOn(":flink-dist").getAllProjectDependencies()
        configurations[configurationName].dependencies.filterIsInstance<ProjectDependency>().flatMap {
            if (distProjects.contains(it)) {
                return@flatMap listOf(it)
            }
            it.dependencyProject.getDistProjectDependencies(configurationName)
        }.toSet()
    }
