rootProject.name = "voidheart"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

include("voidheart-commands")
include("voidheart-core-folia")
include("voidheart-commands-paper")
include("voidheart-scoreboard-folia")