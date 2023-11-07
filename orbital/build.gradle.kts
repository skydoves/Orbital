import com.skydoves.orbital.Configuration
import com.skydoves.orbital.Versions

plugins {
  id("com.android.library")
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  id("org.jetbrains.dokka")
  id("binary-compatibility-validator")
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs += listOf(
      "-Xexplicit-api=strict",
      "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi"
    )
  }
}
