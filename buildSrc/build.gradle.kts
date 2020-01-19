import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    maven(url = uri("https://plugins.gradle.org/m2/"))
    jcenter()
}

dependencies {
    api("com.github.jengelman.gradle.plugins:shadow:5.1.0")
    implementation(group = "com.github.jk1", name = "gradle-license-report", version = "1.11")
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.9")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
}