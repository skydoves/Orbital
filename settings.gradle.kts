pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2/") }
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
  repositories {
    google()
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2/") }
  }
}
rootProject.name = "OrbitalDemo"
include(":app")
include(":orbital")
include(":benchmark-app")
include(":benchmark")
