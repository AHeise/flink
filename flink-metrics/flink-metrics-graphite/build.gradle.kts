dependencies {
    implementation(project(":flink-metrics:flink-metrics-dropwizard"))
    implementation(Libs.metrics_core)
    implementation(Libs.metrics_graphite)
    implementation(project(":flink-annotations"))
    implementation(project(":flink-metrics:flink-metrics-core"))
}

description = "flink-metrics-graphite"
