import com.skydoves.orbital.Configuration
import com.skydoves.orbital.Versions

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  kotlin("multiplatform")
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.jetbrains.compose.get().pluginId)
  id(libs.plugins.baseline.profile.get().pluginId)
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

  androidTarget()
  jvm()

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
    kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
  }

  lint {
    abortOnError = false
  }
}

baselineProfile {
  filter {
    include("com.skydoves.orbital.**")
  }
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
