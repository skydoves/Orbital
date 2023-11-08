import com.skydoves.orbital.Configuration

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  kotlin("multiplatform")
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.jetbrains.compose.get().pluginId)
  id(libs.plugins.nexus.plugin.get().pluginId)
  id(libs.plugins.baseline.profile.get().pluginId)
}

apply(from = "${rootDir}/scripts/publish-module.gradle.kts")

mavenPublishing {
  val artifactId = "orbital"
  coordinates(
    Configuration.artifactGroup,
    artifactId,
    rootProject.extra.get("libVersion").toString()
  )

  pom {
    name.set(artifactId)
    description.set("Jetpack Compose Multiplatform library that allows you to implement dynamic transition animations such as shared element transitions.")
  }
}

kotlin {
  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
    macosArm64()
  ).forEach {
    it.binaries.framework {
      baseName = "common"
    }
  }

  js(IR) {
    browser()
    binaries.executable()
  }

  jvm()
  androidTarget()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(compose.ui)
        implementation(compose.animation)
      }
    }
  }

  explicitApi()
}

android {
  compileSdk = Configuration.compileSdk
  namespace = "com.skydoves.orbital"
  defaultConfig {
    minSdk = Configuration.minSdk
  }

  buildFeatures {
    compose = true
    buildConfig = false
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
  }

  packaging {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
  }

  lint {
    abortOnError = false
  }
}

baselineProfile {
  baselineProfileOutputDir = "../../src/androidMain"
  filter {
    include("com.skydoves.orbital.**")
  }
}

dependencies {
  baselineProfile(project(":benchmark"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs += listOf(
      "-Xexplicit-api=strict",
      "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi"
    )
  }
}
