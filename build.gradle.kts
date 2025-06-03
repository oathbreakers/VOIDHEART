plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization) apply false
    id("com.gradleup.shadow") version "8.3.5" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16" apply false
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    group = "oathbreakers"
    version = "0.0.0-DEV"

    java {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
    }

    kotlin {
        jvmToolchain(21)
    }
}
