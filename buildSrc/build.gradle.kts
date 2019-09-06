plugins {
    `kotlin-dsl`
}

repositories {
    maven(url = uri("https://plugins.gradle.org/m2/"))
    jcenter()
}

dependencies {
    api("com.github.jengelman.gradle.plugins:shadow:5.1.0")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}