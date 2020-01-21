dependencies {
    implementation(project(":flink-metrics:flink-metrics-dropwizard"))
    flinkDependencyGroup(stringProperty("metrics.version")) {
        implementation(Libs.metrics_core)
        implementation(Libs.metrics_graphite)
    }
    compileOnly(project(":flink-annotations"))
    implementation(project(":flink-metrics:flink-metrics-core"))
}

description = "flink-metrics-graphite"
