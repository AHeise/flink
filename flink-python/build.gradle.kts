dependencies {
    implementation(Libs.py4j)
    implementation(Libs.pyrolite)
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_cli)
    implementation(project(":flink-core"))
    implementation(project(":flink-java"))
    implementation(project(":flink-clients"))
    implementation(project(":flink-streaming-java"))
    implementation(project(":flink-table:flink-table-common"))
}

description = "flink-python"

tasks.withType<ShadowJar> {
    relocate("py4j", "org.apache.flink.api.python.shaded.py4j")
    relocate("net.razorvine", "org.apache.flink.api.python.shaded.net.razorvine")
}