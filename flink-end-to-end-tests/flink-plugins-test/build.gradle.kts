dependencies {
    implementation(project(":flink-core"))
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("flink-dummy-fs")
}