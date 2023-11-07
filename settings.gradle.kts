pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
  repositories {
    google()
    mavenCentral()
  }
}
rootProject.name = "OrbitalDemo"
include(":app")
include(":orbital")
include(":benchmark-app")
include(":benchmark")
