plugins {
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow")

    kotlin("plugin.serialization")
}

dependencies {
    paperweight.foliaDevBundle("1.21.4-R0.1-SNAPSHOT")

    implementation(libs.ktoml.core)
    implementation(libs.koin.core)
    implementation(libs.koin.logger)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.migration)
    implementation(libs.flyway)
    implementation(libs.flyway.pg)
    implementation(libs.postgres)
    implementation(libs.hikaricp)

    implementation(project(":voidheart-commands-paper"))
    implementation(project(":voidheart-scoreboard-folia"))
}

runPaper.folia.registerTask()