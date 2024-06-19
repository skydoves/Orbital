import com.skydoves.orbital.Configuration
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.jetbrains.compose.get().pluginId)
  id(libs.plugins.compose.compiler.get().pluginId)
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
  androidTarget { publishLibraryVariants("release") }

  jvm("desktop")

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  macosX64()
  macosArm64()

  js {
    browser()
    nodejs {
      testTask {
        useMocha {
          timeout = "60s"
        }
      }
    }
    binaries.executable()
    binaries.library()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      testTask {
        enabled = false
      }
    }
    nodejs {
      testTask {
        enabled = false
      }
    }
    binaries.executable()
    binaries.library()
  }

  applyDefaultHierarchyTemplate()

  sourceSets {
    all {
      languageSettings.optIn("androidx.compose.ui.ExperimentalComposeUiApi")
    }
    val commonMain by getting {
      dependencies {
        implementation(compose.ui)
        implementation(compose.animation)
        implementation(compose.runtime)
      }
    }
  }

  explicitApi()
  applyKotlinJsImplicitDependencyWorkaround()
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

// https://youtrack.jetbrains.com/issue/KT-56025
fun Project.applyKotlinJsImplicitDependencyWorkaround() {
  tasks {
    val configureJs: Task.() -> Unit = {
      dependsOn(named("jsDevelopmentLibraryCompileSync"))
      dependsOn(named("jsDevelopmentExecutableCompileSync"))
      dependsOn(named("jsProductionLibraryCompileSync"))
      dependsOn(named("jsProductionExecutableCompileSync"))
      dependsOn(named("jsTestTestDevelopmentExecutableCompileSync"))

      dependsOn(getByPath(":orbital:jsDevelopmentLibraryCompileSync"))
      dependsOn(getByPath(":orbital:jsDevelopmentExecutableCompileSync"))
      dependsOn(getByPath(":orbital:jsProductionLibraryCompileSync"))
      dependsOn(getByPath(":orbital:jsProductionExecutableCompileSync"))
      dependsOn(getByPath(":orbital:jsTestTestDevelopmentExecutableCompileSync"))
    }
    named("jsBrowserProductionWebpack").configure(configureJs)
    named("jsBrowserProductionLibraryDistribution").configure(configureJs)
    named("jsNodeProductionLibraryDistribution").configure(configureJs)

    val configureWasmJs: Task.() -> Unit = {
      dependsOn(named("wasmJsDevelopmentLibraryCompileSync"))
      dependsOn(named("wasmJsDevelopmentExecutableCompileSync"))
      dependsOn(named("wasmJsProductionLibraryCompileSync"))
      dependsOn(named("wasmJsProductionExecutableCompileSync"))
      dependsOn(named("wasmJsTestTestDevelopmentExecutableCompileSync"))

      dependsOn(getByPath(":orbital:wasmJsDevelopmentLibraryCompileSync"))
      dependsOn(getByPath(":orbital:wasmJsDevelopmentExecutableCompileSync"))
      dependsOn(getByPath(":orbital:wasmJsProductionLibraryCompileSync"))
      dependsOn(getByPath(":orbital:wasmJsProductionExecutableCompileSync"))
      dependsOn(getByPath(":orbital:wasmJsTestTestDevelopmentExecutableCompileSync"))
    }
    named("wasmJsBrowserProductionWebpack").configure(configureWasmJs)
    named("wasmJsBrowserProductionLibraryDistribution").configure(configureWasmJs)
    named("wasmJsNodeProductionLibraryDistribution").configure(configureWasmJs)
  }
}
