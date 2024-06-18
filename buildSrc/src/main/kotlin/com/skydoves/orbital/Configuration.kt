package com.skydoves.orbital

object Configuration {
  const val compileSdk = 34
  const val targetSdk = 33
  const val minSdk = 21
  const val majorVersion = 0
  const val minorVersion = 4
  const val patchVersion = 0
  const val versionName = "$majorVersion.$minorVersion.$patchVersion"
  const val versionCode = 14
  const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
  const val artifactGroup = "com.github.skydoves"
}
