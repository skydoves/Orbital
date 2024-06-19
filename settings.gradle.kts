pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven(url = "https://plugins.gradle.org/m2/")
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven(url = "https://plugins.gradle.org/m2/")
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}
rootProject.name = "OrbitalDemo"
include(":app")
include(":orbital")
include(":benchmark-app")
include(":benchmark")
