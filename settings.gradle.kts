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
        maven("https://dl.google.com/dl/android/maven2") {
            metadataSources {
                mavenPom()
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Guide Trade AI"
include(":app")
