dependencies {
    api(project(":flink-connectors:flink-connector-elasticsearch6"))
}

description = "flink-sql-connector-elasticsearch6"

tasks.withType<ShadowJar> {
    // Unless otherwise noticed these filters only serve to reduce the size of the resulting  jar by removing unnecessary files
    // org.elasticsearch:elasticsearch
    exclude("config/**")
    exclude("modules.txt")
    exclude("plugins.txt")
    exclude("org/joda/**")
    // org.elasticsearch.client:elasticsearch-rest-high-level-client
    exclude("forbidden/**")
    // org.apache.httpcomponents:httpclient
    exclude("mozilla/**")
    // org.apache.lucene:lucene-analyzers-common
    exclude("org/tartarus/**")

    // Force relocation of all Elasticsearch dependencies.
    relocate("org.apache.commons", "org.apache.flink.elasticsearch6.shaded.org.apache.commons")
    relocate("org.apache.http", "org.apache.flink.elasticsearch6.shaded.org.apache.http")
    relocate("org.apache.lucene", "org.apache.flink.elasticsearch6.shaded.org.apache.lucene")
    relocate("org.elasticsearch", "org.apache.flink.elasticsearch6.shaded.org.elasticsearch")
    relocate("org.apache.logging", "org.apache.flink.elasticsearch6.shaded.org.apache.logging")
    relocate("com.fasterxml.jackson", "org.apache.flink.elasticsearch6.shaded.com.fasterxml.jackson")
}