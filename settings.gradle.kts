pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = uri("libs/local-maven"))
        google()
        mavenCentral()
    }
}

rootProject.name = "Guide Trade AI"
include(":app")
