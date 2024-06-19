import com.skydoves.orbital.Configuration

plugins {
  id(libs.plugins.android.application.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.compose.compiler.get().pluginId)
  id(libs.plugins.baseline.profile.get().pluginId)
}

android {
  namespace = "com.skydoves.orbital.benchmark.app"
  compileSdk = Configuration.compileSdk

  defaultConfig {
    applicationId = "com.skydoves.orbital.benchmark.app"
    minSdk = 23
    targetSdk = Configuration.targetSdk
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = libs.versions.jvmTarget.get()
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  lint {
    abortOnError = false
  }

  buildTypes {
    debug {
      isDebuggable = true
    }
    create("benchmark") {
      initWith(buildTypes.getByName("release"))
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
    }
  }
}

dependencies {
  implementation(project(":orbital"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.constraintlayout)
  implementation(libs.androidx.compose.material.iconsExtended)

  baselineProfile(project(":benchmark"))
}