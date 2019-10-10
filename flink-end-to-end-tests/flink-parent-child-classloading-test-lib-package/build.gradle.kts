description = "flink-parent-child-classloading-test-lib-package"

tasks.withType<ShadowJar> {
    archiveBaseName.set("LibPackage")
}