dependencies {
    implementation(Libs.tpch)
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("TpchTestProgram")
}