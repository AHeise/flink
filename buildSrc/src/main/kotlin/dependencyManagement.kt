import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.*

data class Module(val group: String, val name: String)

interface DependencyGroupManagementHandler {
    fun dependency(dependencyNotation: Any, configuration: String = "all", dependencyConfiguration: (ExternalModuleDependency.() -> Unit)? = null): DependencyConstraint
    fun dependency(group: String, name: String, configuration: String = "all", dependencyConfiguration: (ExternalModuleDependency.() -> Unit)? = null): DependencyConstraint =
        dependency(mapOf("group" to group, "name" to name), configuration, dependencyConfiguration)
}

interface DependencyManagementHandler {
    fun dependencyGroup(version: String, groupConfiguration: DependencyGroupManagementHandler.() -> Unit)

    fun dependency(dependencyNotation: Any, version: String, configuration: String = "all", dependencyConfiguration: (ExternalModuleDependency.() -> Unit)? = null): DependencyConstraint

    fun dependency(group: String, name: String, version: String, configuration: String = "all", dependencyConfiguration: (ExternalModuleDependency.() -> Unit)? = null): DependencyConstraint=
        dependency(mapOf("group" to group, "name" to name), version, configuration, dependencyConfiguration)
}

fun Project.flinkDependencyManagement(dependenciesHandler: Action<DependencyManagementHandler>) {
    // only configure java/scala projects
    plugins.withType<JavaLibraryPlugin> {
        val all = configurations.maybeCreate("all") {
            isCanBeResolved = false
            isCanBeConsumed = false
        }
        configurations["api"].extendsFrom(all)
        configurations["annotationProcessor"].extendsFrom(all)
        configurations["testAnnotationProcessor"].extendsFrom(all)
        val cachingDependencyHandler = rootProject.extra.getOrPut("cachingDependencyHandler") {
            CachingDependencyHandler(rootProject.dependencies.constraints)
        }
        val managedDependencies = cachingDependencyHandler.getManagedDependencies(dependenciesHandler)
        val configurationConstraints = managedDependencies.configurationConstraints.mapKeys { configurations[it.key] }
        configurationConstraints.forEach { (conf, constraints) ->
            if (conf.dependencyConstraints.isNotEmpty()) {
                // remove overwritten entries
                val constraintIndex = constraints.constraints.map { Module(it.group, it.name) }.toSet()
                conf.dependencyConstraints.removeIf { constraintIndex.contains(Module(it.group, it.name)) }
            }
            conf.dependencyConstraints += constraints.constraints
        }
        configurations
                .filter { conf -> conf.isCanBeResolved }
                .forEach { conf ->
                    conf.withDependencies {
                        val constraintIndex = configurationConstraints
                                .filterKeys { conf.hierarchy.contains(it) }
                                .flatMap { (conf, constraints) -> constraints.constraints.map { Module(it.group, it.name) to conf } }
                                .toMap(LinkedHashMap())
                        forEach { dependency ->
                            if (dependency is ExternalModuleDependency) {
                                val lookup = Module(dependency.group, dependency.name)
                                constraintIndex[lookup]?.also { dependencyConf ->
                                    requireNotNull(configurationConstraints[dependencyConf]).moduleConfigurer[lookup]?.invoke(dependency)
                                }
                            }
                        }
                    }
                }
        managedDependencies.platformSetups.forEach { it.execute(project) }
    }
}

fun <T> NamedDomainObjectContainer<T>.maybeCreate(name: String, configure: Action<in T>): T =
    findByName(name) ?: create(name, configure)

fun DependencyHandler.flinkDependencyGroup(version: String, groupConfiguration: DependencyHandlerScope.() -> Unit) {
    val dependencyHandler = this
    val groupDependencies = mutableListOf<ModuleVersionSelector>()
    val dependencyIntercepter = object: DependencyHandler by dependencyHandler {
        override fun add(configurationName: String, dependencyNotation: Any): Dependency? =
            dependencyHandler.add(configurationName, dependencyNotation).also {
                require(it is ExternalModuleDependency) { "Can only add modules to group" }
                if (it.versionConstraint.strictVersion.isNullOrBlank()) {
                    it.version {
                        require(version)
                    }
                }
                groupDependencies.add(it)
            }

        override fun add(configurationName: String, dependencyNotation: Any, configureClosure: Closure<*>)=
            dependencyHandler.add(configurationName, dependencyNotation, configureClosure).also {
                require(it is ExternalModuleDependency) { "Can only add modules to group" }
                if (it.versionConstraint.strictVersion.isNullOrBlank()) {
                    it.version {
                        require(version)
                    }
                }
                groupDependencies.add(it)
            }
    }
    groupConfiguration(DependencyHandlerScope.of(dependencyIntercepter))
    setupPlatform(groupDependencies)
}

private data class DependencyConfigurationConstraints(
        val constraints: List<DependencyConstraint>,
        val moduleConfigurer: Map<Module, ExternalModuleDependency.() -> Unit>)

private data class ManagedDependencies(
        val configurationConstraints: Map<String, DependencyConfigurationConstraints>,
        val platformSetups: List<Action<Project>>)

private class CachingDependencyHandler(val constraintHandler: DependencyConstraintHandler) {
    private val cache = mutableMapOf<Any, ManagedDependencies>()

    fun getManagedDependencies(dependenciesHandler: Action<DependencyManagementHandler>): ManagedDependencies =
            cache.getOrPut(dependenciesHandler::class) {
                extractConstraints(dependenciesHandler)
            }

    private fun extractConstraints(dependenciesHandler: Action<DependencyManagementHandler>): ManagedDependencies {
        val handler = DefaultDependencyManagementHandler(constraintHandler)
        dependenciesHandler(handler)
        val constraints = handler.constraints.mapValues { (conf, constraints) ->
            DependencyConfigurationConstraints(
                    constraints,
                    handler.managedDependencyConfigurers[conf]?.dependencyConfigurations ?: mapOf())
        }
        return ManagedDependencies(constraints, handler.platformSetups)
    }
}

private inline class ManagedDependencyConfigurer(val dependencyConfigurations: MutableMap<Module, ExternalModuleDependency.() -> Unit> = mutableMapOf())
private open class DefaultDependencyManagementHandler(private val constraintHandler: DependencyConstraintHandler): DependencyManagementHandler {
    val constraints: MutableMap<String, MutableList<DependencyConstraint>> = mutableMapOf()
    val managedDependencyConfigurers: MutableMap<String, ManagedDependencyConfigurer> = mutableMapOf()
    val platformSetups: MutableList<Action<Project>> = mutableListOf()

    override fun dependencyGroup(version: String, groupConfiguration: DependencyGroupManagementHandler.() -> Unit) {
        DefaultDependencyGroupManagementHandler(this, version).run {
            groupConfiguration(this)
            platformSetups += this
        }
    }

    override fun dependency(dependencyNotation: Any, version: String, configuration: String, dependencyConfiguration: (ExternalModuleDependency.() -> Unit)?): DependencyConstraint =
            addDependencyConstraint(configuration, dependencyNotation, version, dependencyConfiguration)

    open fun addDependencyConstraint(configurationName: String, dependencyNotation: Any, version: String, dependencyConfiguration: (ExternalModuleDependency.() -> Unit)? = null): DependencyConstraint {
        return constraintHandler.create(extractDependencyNotation(dependencyNotation)) {
            version {
                strictly(version)
            }
            constraints.getOrPut(configurationName) { mutableListOf() } += this
            if (dependencyConfiguration != null) {
                val configurer = managedDependencyConfigurers.getOrPut(configurationName) {
                    ManagedDependencyConfigurer()
                }
                configurer.dependencyConfigurations[Module(group, name)] = dependencyConfiguration
            }
        }
    }

    private fun extractDependencyNotation(dependencyNotation: Any) =
        if (dependencyNotation is ExternalModuleDependency)
            mapOf("group" to dependencyNotation.group, "name" to dependencyNotation.name)
        else dependencyNotation
}

private class DefaultDependencyGroupManagementHandler(private val managementHandler: DependencyManagementHandler, private val version: String) : DependencyGroupManagementHandler, Action<Project> {
    private val constraints = mutableListOf<DependencyConstraint>()

    override fun dependency(dependencyNotation: Any, configuration: String, dependencyConfiguration: (ExternalModuleDependency.() -> Unit)?): DependencyConstraint =
        managementHandler.dependency(dependencyNotation, version, configuration, dependencyConfiguration).also {
            constraints.add(it)
        }

    override fun execute(project: Project) {
        project.dependencies.setupPlatform(constraints)
    }
}

private fun DependencyHandler.setupPlatform(dependencies: List<ModuleVersionSelector>) {
    check(dependencies.isNotEmpty()) { "No dependencies added" }
    val group = org.apache.commons.lang3.StringUtils.getCommonPrefix(*dependencies.map { it.group }.distinct().toTypedArray())
    components.all(GroupAlignmentRule::class) {
        params(group, dependencies.map { it.name })
    }
}

private fun ModuleVersionSelector.asExternalModule(conf: String): Dependency =
    DefaultExternalModuleDependency(name, group, version, conf)

private open class GroupAlignmentRule @javax.inject.Inject constructor(private val group: String, dependencies: List<String>): ComponentMetadataRule {
    private val dependencyIndex: Set<String> = dependencies.toSet()

    override fun execute(ctx: ComponentMetadataContext) {
        if (ctx.details.id.group == group && dependencyIndex.contains(ctx.details.id.name)) {
            ctx.details.belongsTo("$group:${group.substringAfterLast('.')}-platform:${ctx.details.id.version}")
        }
    }
}
