plugins {
    id("java-library")
}

flinkCreateTestJar()

dependencies {
    api(project(":flink-core"))
    api(Libs.kryo)

    implementation(project(":flink-annotations"))
    implementation(Libs.jsr305)
    implementation(Libs.slf4j_api)
    implementation(Libs.flink_shaded_asm_6)
    implementation(Libs.commons_lang3)
    implementation(Libs.commons_math3)

    testImplementation(project(":flink-test-utils-parent:flink-test-utils-junit"))
    testImplementation(project(":flink-metrics:flink-metrics-core"))
    testImplementation(Libs.junit)
    testImplementation(Libs.hamcrest_all)
}

description = "flink-java"
