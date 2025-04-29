plugins {
    id("java")
    kotlin("jvm") version "2.1.20"
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }

    group = "oathbreakers"
    version = "0.0.0-DEV"
}
