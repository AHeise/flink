import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.ComponentMetadataHandler
import org.gradle.api.artifacts.dsl.ComponentModuleMetadataHandler
import org.gradle.api.artifacts.dsl.DependencyConstraintHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.query.ArtifactResolutionQuery
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.artifacts.transform.TransformSpec
import org.gradle.api.artifacts.transform.VariantTransform
import org.gradle.api.artifacts.type.ArtifactTypeContainer
import org.gradle.api.attributes.AttributesSchema
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.*

object CachingDependencyHandler {

}

fun Project.flinkDependencyManagement(dependenciesHandler: Action<DependencyManagementHandler>) {
    println("flinkDependencyManagement ${System.identityHashCode(dependenciesHandler)}")
    // only configure java/scala projects
    plugins.withType<JavaLibraryPlugin> {
        dependencies {
            constraints {
                dependenciesHandler(DependencyManagementHandler(this@flinkDependencyManagement, this))
            }
        }
    }
}

fun Project.flinkDependencyGroup(version: String, groupConfiguration: DependencyGroupManagementHandler.() -> Unit) {
    val project = this
    dependencies {
        val dependencyHandler = this
        constraints {
//            val managementHandler = object : DependencyManagementHandler(project, this) {
//                override fun addDependencyConstraint(configuration: String, dependencyNotation: Any, version: String, dependencyConfiguration: (ExternalModuleDependency.() -> Unit)?): DependencyConstraint {
//                    dependencyHandler.add(configuration, "$dependencyNotation:$version")
//                    return super.addDependencyConstraint(configuration, dependencyNotation, version, dependencyConfiguration)
//                }
//            }
            DependencyGroupManagementHandler(project, this, version).run {
                groupConfiguration(this)
                setupPlatform()
            }
        }
    }
}

//fun Project.flinkStrictVersion(version: String, configuration: DependencyHandlerScope.() -> Unit) {
//    dependencies {
//        val originalHandler = this
//
//        val handler = object : DependencyHandlerScope by originalHandler {
//            override fun add(configurationName: String, dependencyNotation: Any): Dependency? {
//                originalHandler.add(configurationName, dependencyNotation) {
//                    version {
//                        strictly(version)
//                    }
//                    project.configurations
//                }
//            }
//
//            override fun add(configurationName: String, dependencyNotation: Any, configureClosure: Closure<*>): Dependency {
//                originalHandler.add(configurationName, dependencyNotation) {
//                    version {
//                        strictly(version)
//                    }
//                    configureClosure(this)
//                }
//            }
//        }
//        configuration(handler)
//    }
//}

///**
// * Ensures all artifacts of the dependency/group to be of the specific version
// */
fun Project.flinkForceDependencyVersion(group: String? = null, name: String? = null, version: String) {
//    dependencies {
//        constraints {
//
//        }
//    }
    configurations.forEach { conf ->
        conf.withDependencies {
            val deps = this
            deps.filter { (group?.equals(it.group) ?: true) && (name?.equals(it.name) ?: true) }
                .filterIsInstance(ExternalDependency::class.java)
                .forEach { d ->
                    d.version {
                        strictly(version)
                    }
                }
        }
    }
//    dependencies {
//        constraints {x
//            components.all(ForcedVersionRule::class) {
//                params(group, name, version)
//            }
//        }
//    }
//    dependencies {
//        constraints {
//            val constraint = this.create(mapOf("group" to group, "name" to name))
//            constraint.version {
//                strictly(version)
//            }
//            configurations.forEach { conf ->
//                add(conf.name, constraint)
//            }
//        }
//    }
//    configurations.all {
//        resolutionStrategy.eachDependency {
//            if ((group == null || target.group == group) && (name == null || target.name == name)) {
//                useVersion(version.toString())
//            }
//        }
//    }
}

open class DependencyManagementHandler(val project: Project, private val constraintHandler: DependencyConstraintHandler): DependencyHandlerAdapter {
    private val managedDependencyConfigurers: MutableMap<String, ManagedDependencyConfigurer> = mutableMapOf()

    fun flinkDependencyGroup(version: String, groupConfiguration: DependencyGroupManagementHandler.() -> Unit) {
        DependencyGroupManagementHandler(project, constraintHandler, version).run {
            groupConfiguration(this)
            setupPlatform()
        }
    }

    operator fun String.invoke(dependencyNotation: String, version: String): DependencyConstraint =
        addDependencyConstraint(this, dependencyNotation, version)

    operator fun String.invoke(dependencyNotation: String, version: String, dependencyConfiguration: ExternalModuleDependency.() -> Unit): DependencyConstraint =
        addDependencyConstraint(this, dependencyNotation, version, dependencyConfiguration)

    private fun splitNotation(dependencyNotation: Any): Pair<String, String> {
        val parts = dependencyNotation.toString().split(':')
        check(parts.size > 3) { "$dependencyNotation must have version" }
        return parts[0] + parts[1] to parts[2]
    }

    override fun add(configurationName: String, dependencyNotation: Any): Dependency {
        val (groupModule, version) = splitNotation(dependencyNotation)
        return configurationName(groupModule, version).asExternalModule(configurationName)
    }

    override fun add(configurationName: String, dependencyNotation: Any, configureClosure: Closure<*>): Dependency {
        val (groupModule, version) = splitNotation(dependencyNotation)
        return configurationName(groupModule, version) {
            configureClosure(this)
        }.asExternalModule(configurationName)
    }

    open fun addDependencyConstraint(configurationName: String, dependencyNotation: Any, version: String, dependencyConfiguration: (ExternalModuleDependency.() -> Unit)? = null): DependencyConstraint {
        return this@DependencyManagementHandler.constraintHandler.add(configurationName, dependencyNotation) {
            version {
                strictly(version)
            }
            if (dependencyConfiguration != null) {
                val configurer = managedDependencyConfigurers.getOrPut(configurationName) {
                    ManagedDependencyConfigurer.forConfiguration(project.configurations[configurationName])
                }
                configurer[this] = dependencyConfiguration
            }
        }
    }
}

interface DependencyHandlerAdapter : DependencyHandler {
    override fun platform(notation: Any): Dependency = TODO("unsupported")

    override fun platform(notation: Any, configureAction: Action<in Dependency>): Dependency = TODO("unsupported")

    override fun create(dependencyNotation: Any): Dependency = TODO("unsupported")

    override fun create(dependencyNotation: Any, configureClosure: Closure<*>): Dependency = TODO("unsupported")

    override fun testFixtures(notation: Any): Dependency = TODO("unsupported")

    override fun testFixtures(notation: Any, configureAction: Action<in Dependency>): Dependency = TODO("unsupported")

    override fun getExtensions(): ExtensionContainer = TODO("unsupported")

    override fun gradleApi(): Dependency = TODO("unsupported")

    override fun components(configureAction: Action<in ComponentMetadataHandler>) = TODO("unsupported")

    override fun createArtifactResolutionQuery(): ArtifactResolutionQuery = TODO("unsupported")

    override fun getModules(): ComponentModuleMetadataHandler = TODO("unsupported")

    override fun getArtifactTypes(): ArtifactTypeContainer = TODO("unsupported")

    override fun modules(configureAction: Action<in ComponentModuleMetadataHandler>) = TODO("unsupported")

    override fun artifactTypes(configureAction: Action<in ArtifactTypeContainer>) = TODO("unsupported")

    override fun localGroovy(): Dependency = TODO("unsupported")

    override fun getComponents(): ComponentMetadataHandler = TODO("unsupported")

    override fun enforcedPlatform(notation: Any): Dependency = TODO("unsupported")

    override fun enforcedPlatform(notation: Any, configureAction: Action<in Dependency>): Dependency = TODO("unsupported")

    override fun getConstraints(): DependencyConstraintHandler = TODO("unsupported")

    override fun registerTransform(registrationAction: Action<in VariantTransform>) = TODO("unsupported")

    override fun <T : TransformParameters?> registerTransform(actionType: Class<out TransformAction<T>>, registrationAction: Action<in TransformSpec<T>>) = TODO("unsupported")

    override fun gradleTestKit(): Dependency = TODO("unsupported")

    override fun constraints(configureAction: Action<in DependencyConstraintHandler>) = TODO("unsupported")

    override fun getAttributesSchema(): AttributesSchema = TODO("unsupported")

    override fun module(notation: Any): Dependency = TODO("unsupported")

    override fun module(notation: Any, configureClosure: Closure<*>): Dependency = TODO("unsupported")

    override fun project(notation: MutableMap<String, *>): Dependency = TODO("unsupported")

    override fun attributesSchema(configureAction: Action<in AttributesSchema>): AttributesSchema = TODO("unsupported")
}

class DependencyGroupManagementHandler(private val project: Project, private val constraintHandler: DependencyConstraintHandler, private val version: String) : DependencyHandlerAdapter {
    private val dependencies = mutableListOf<ModuleVersionSelector>()
    private val managementHandler = object: DependencyManagementHandler(project, constraintHandler) {
        override fun addDependencyConstraint(configurationName: String, dependencyNotation: Any, version: String, dependencyConfiguration: (ExternalModuleDependency.() -> Unit)?): DependencyConstraint =
            super.addDependencyConstraint(configurationName, dependencyNotation, version, dependencyConfiguration).also {
                dependencies.add(it)
            }
    }

    operator fun String.invoke(dependencyNotation: String): DependencyConstraint =
        managementHandler.addDependencyConstraint(this, dependencyNotation, version)

    operator fun String.invoke(dependencyNotation: String, dependencyConfiguration: ExternalModuleDependency.() -> Unit): DependencyConstraint =
        managementHandler.addDependencyConstraint(this, dependencyNotation, version, dependencyConfiguration)

    override fun add(configurationName: String, dependencyNotation: Any): Dependency =
        managementHandler.addDependencyConstraint(configurationName, dependencyNotation, version)
                .asExternalModule(configurationName)

    override fun add(configurationName: String, dependencyNotation: Any, configureClosure: Closure<*>): Dependency =
        managementHandler.addDependencyConstraint(configurationName, dependencyNotation, version) {
            configureClosure(this)
        }.asExternalModule(configurationName)

    fun setupPlatform() {
        check(dependencies.isNotEmpty()) { "No dependencies added" }
        val group = dependencies[0].group
        check(dependencies.all { it.group == group }) {
            "Different group in dependency group (could be fine but needs to be implemented) ${dependencies.map { it.group }}"
        }
        managementHandler.project.dependencies.components.all(GroupAlignmentRule::class) {
            params(group, dependencies.map { it.name })
        }
    }
}

private fun ModuleVersionSelector.asExternalModule(conf: String): Dependency =
    DefaultExternalModuleDependency(name, group, version, conf)

private class ManagedDependencyConfigurer
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

private open class GroupAlignmentRule @javax.inject.Inject constructor(private val group: String, dependencies: List<String>): ComponentMetadataRule {
    private val dependencyIndex: Set<String> = dependencies.toSet()

    override fun execute(ctx: ComponentMetadataContext) {
        if (ctx.details.id.group == group && dependencyIndex.contains(ctx.details.id.name)) {
            ctx.details.belongsTo("$group:${group.substringAfterLast('.')}-platform:${ctx.details.id.version}")
        }
    }
}
//
//private open class ForcedVersionRule @javax.inject.Inject constructor(private val group: String?, private val name: String?, private val version: String): ComponentMetadataRule {
//    override fun execute(ctx: ComponentMetadataContext) {
//        if ((group?.equals(ctx.details.id.group) ?: true) && (name?.equals(ctx.details.id.name) ?: true)) {
//            ctx.details.id.version = version
//        }
//    }
//}