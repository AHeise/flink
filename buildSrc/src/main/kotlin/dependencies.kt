import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.exclude
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
