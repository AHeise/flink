import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.internal.impldep.org.junit.platform.engine.discovery.ModuleSelector
import org.gradle.kotlin.dsl.*

fun DependencyHandler.`testApi`(dependencyNotation: Any): Dependency? =
        add("testApi", dependencyNotation)

fun Project.flinkRegisterTestApi() {
    configurations.register("testApi") {
        extendsFrom(configurations["api"])
        configurations["testImplementation"].extendsFrom(this)
        isTransitive = true
    }
}

class ManagedDependencyConfigurer
        (private val dependencyConfigurations: MutableMap<ModuleVersionSelector, ExternalModuleDependency.() -> Unit> = mutableMapOf())
        : MutableMap<ModuleVersionSelector, ExternalModuleDependency.() -> Unit> by dependencyConfigurations {

    fun dependencyAdded(dependency: Dependency) {
        if (dependency is ExternalModuleDependency) {
            dependencyConfigurations[dependency]
                    ?.invoke(dependency)
        }
    }

    companion object {
        fun forConfiguration(configuration: Configuration): ManagedDependencyConfigurer =
            ManagedDependencyConfigurer().also { configurer ->
                configuration.dependencies.whenObjectAdded {
                    configurer.dependencyAdded(this)
                }
            }
    }
}

class DependencyManagementHandler(val project: Project, val constraintHandler: DependencyConstraintHandler) {
    val managedDependencyConfigurers: MutableMap<String, ManagedDependencyConfigurer> = mutableMapOf()

    inline operator fun String.invoke(dependencyNotation: String, version: Any?): DependencyConstraint =
        this@DependencyManagementHandler.constraintHandler.add(this, dependencyNotation) {
            version {
                strictly(version!!.toString())
            }
        }

    inline operator fun String.invoke(dependencyNotation: String, version: Any?, noinline dependencyConfiguration: ExternalModuleDependency.() -> Unit): DependencyConstraint {
        val constraint = invoke(dependencyNotation, version)
        val configurer = managedDependencyConfigurers.getOrPut(this) {
            ManagedDependencyConfigurer.forConfiguration(project.configurations[this])
        }
        configurer[constraint] = dependencyConfiguration
        return constraint
    }
}

fun Project.flinkDependencyManagement(dependenciesHandler: Action<DependencyManagementHandler>) {
    subprojects {
        // only configure java/scala projects
        plugins.withType<JavaLibraryPlugin> {
            dependencies {
                constraints {
                    dependenciesHandler(DependencyManagementHandler(this@subprojects, this))
                }
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

fun Project.getAllProjectDependencies(): Set<ProjectDependency> =
    extra.properties.getOrPut("allProjectDependencies") {
        configurations["runtimeElements"].allDependencies.filterIsInstance<ProjectDependency>().flatMap {
            setOf(it) + it.dependencyProject.getAllProjectDependencies()
        }.toSet()
    } as Set<ProjectDependency>

fun Project.getDistProjectDependencies(configurationName: String): Set<ProjectDependency> =
    extra.properties.getOrPut("${configurationName}DistProjectDependencies") {
        val distProjects = project.evaluationDependsOn(":flink-dist").getAllProjectDependencies()
        configurations[configurationName].dependencies.filterIsInstance<ProjectDependency>().flatMap {
            if (distProjects.contains(it)) {
                return@flatMap listOf(it)
            }
            it.dependencyProject.getDistProjectDependencies(configurationName)
        }.toSet()
    } as Set<ProjectDependency>
