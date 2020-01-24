/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.flink.gradle.CacheableApacheNoticeResourceTransformer
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.*
import org.gradle.api.tasks.bundling.Jar

// use typealias to keep customized shading options shorter
typealias ShadowJar = com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

private const val SHADE = "shade"

const val PUBLISH = "publish"

fun Project.flinkSetupShading(): TaskProvider<ShadowJar> {
    apply(plugin = "com.github.johnrengelman.shadow")

    val shade = configurations.create(SHADE) {
        configurations["compileClasspath"].extendsFrom(this)
        configurations["testCompileOnly"].extendsFrom(this)
        configurations["testRuntimeOnly"].extendsFrom(this)
    }

    val shadowJar by tasks.existing(ShadowJar::class)

    abstract class NoopService: BuildService<BuildServiceParameters.None>
    val exclusiveManyFiles = gradle.sharedServices.registerIfAbsent("exclusiveManyFiles", NoopService::class) {
        maxParallelUsages.set(2)
    }
    tasks.named<ShadowJar>("shadowJar") {
        configurations = listOf(shade)
        isZip64 = true
        usesService(exclusiveManyFiles)

        // remove 'all' classifier, we want to replace the original jar by the shaded version
        archiveClassifier.set(null as String?)
        // publish still uses old classifier
        @Suppress("DEPRECATION")
        classifier = null

        // transformations
        mergeServiceFiles()
        transform(CacheableApacheNoticeResourceTransformer().apply {
            projectName = "Apache Flink"
        })

        // global excludes
        exclude("log4j.properties", "log4j-test.properties")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

        // exclude( com.google.code.findbugs:jsr305 )
    }

    // disable regular jar task
    tasks.named<Jar>("jar").configure {
        enabled = false
    }
    tasks.withType<Jar> {
        outputs.cacheIf { true }
    }

    // remove the jar from the default artifacts, replace it subsequently with the shaded jar
    // this jar will be used for downstream project/tasks and publishing
    val jarConfigurations = listOf(JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME,
            @Suppress("DEPRECATION")
            JavaPlugin.RUNTIME_CONFIGURATION_NAME,
            JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME,
            "archives")

    val artifact = LazyPublishArtifact(shadowJar)
    jarConfigurations.forEach {
        val configuration = configurations[it]
        configuration.artifacts.clear()
        configuration.artifacts.add(artifact)
    }

    val classesConfigurations = listOf(JavaPlugin.API_ELEMENTS_CONFIGURATION_NAME, JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME)
    classesConfigurations.forEach { compiledConfiguration ->
        configurations[compiledConfiguration].outgoing.variants.removeIf {
            it.attributes.getAttribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE)?.name == LibraryElements.CLASSES
        }
    }

    configurations["default"].setExtendsFrom(listOf(configurations["archives"]))

    // shade and api both extend from all for common constraints/rules
    shade.extendsFrom(configurations.maybeCreate("root") {
        isCanBeResolved = false
        isCanBeConsumed = false
    })

    listOf("api", "implementation").forEach { conf ->
        // exclude dist dependencies, just before shade is resolved
        shade.withDependencies {
            this.filterIsInstance<ProjectDependency>().forEach { shadedDependency ->
                shadedDependency.dependencyProject.getDistProjectDependencies(conf).forEach {
                    shadedDependency.exclude(mapOf("group" to it.group, "module" to it.name))
                }
            }
        }
        configurations[conf].withDependencies {
            val excluded = shade.allDependencies.filterIsInstance<ProjectDependency>().flatMap {
                it.dependencyProject.getDistProjectDependencies(conf)
            }.toSet()
            excluded.forEach {
                add(it)
            }
        }
    }

    return shadowJar
}
//
//
//inline fun ShadowJar.overwriteShadedFiles(configuration: PatternSet.() -> Unit) {
//    val overwrittenPattern = PatternSet()
//    configuration.invoke(overwrittenPattern)
//    val spec = overwrittenPattern.asIncludeSpec
//    transform(object : com.github.jengelman.gradle.plugins.shadow.transformers.Transformer {
//        override fun canTransformResource(element: FileTreeElement?): Boolean {
//            return element is com.github.jengelman.gradle.plugins.shadow.tasks.ShadowCopyAction.ArchiveFileTreeElement
//                    && spec.isSatisfiedBy(element)
//        }
//
//        override fun hasTransformedResource(): Boolean {
//            return false
//        }
//
//        override fun transform(context: TransformerContext?) {
//        }
//
//        override fun modifyOutputStream(jos: ZipOutputStream?, preserveFileTimestamps: Boolean) {
//        }
//    })
//}