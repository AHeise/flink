dependencies {
    implementation(Libs.tpch version "0.10")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("TpchTestProgram")
}