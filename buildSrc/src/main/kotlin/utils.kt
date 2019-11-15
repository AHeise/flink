import groovy.util.Node
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.internal.TaskInternal
import org.gradle.kotlin.dsl.get

val TaskInternal.shouldRun
    get() = enabled && onlyIf.isSatisfiedBy(this)


fun Node.addDependencies(scope: String?, dependencies: Set<Dependency>) {
    dependencies.forEach { dependency ->
        addDependency(scope, dependency)
    }
}

private fun Node.addDependency(scope: String?, dependency: Dependency?) {
    appendNode("dependency").apply {
        when (dependency) {
            is ProjectDependency -> {
                // TEST_JAR configuration has an attribute for classifier
                val classifier = dependency.targetConfiguration?.let {
                    val targetConfiguration = dependency.dependencyProject.configurations[it]
                    targetConfiguration.attributes.getAttribute(CLASSIFIER_ATTRIBUTE)
                }
                appendDependency(dependency, scope, classifier = classifier)
            }
            is ExternalModuleDependency -> {
                if (dependency.artifacts.isEmpty()) {
                    appendDependency(dependency, scope)
                } else {
                    for (artifact in dependency.artifacts) {
                        appendDependency(dependency, scope, extension = artifact.extension, classifier = artifact.classifier)
                    }
                }
            }
        }
    }
}

private fun Node.appendDependency(
        dependency: ModuleDependency,
        scope: String?,
        extension: String = "jar",
        classifier: String? = null) {
    appendNode("groupId", dependency.group)
    appendNode("artifactId", dependency.name)
    appendNode("version", dependency.version)
    if (scope != null) {
        appendNode("scope", scope)
    }
    if (extension != "jar") {
        appendNode("packaging", extension)
    }
    if (classifier != null) {
        appendNode("classifier", classifier)
    }
}

