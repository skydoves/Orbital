<h1 align="center">Orbitary</h1></br>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/Orbitary/actions/workflows/android.yml"><img alt="Build Status" src="https://github.com/skydoves/Orbitary/actions/workflows/android.yml/badge.svg"/></a>
  <a href="https://github.com/skydoves"><img alt="Profile" src="https://skydoves.github.io/badges/skydoves.svg"/></a>
</p><br>

<p align="center">
🪐 Jetpack Compose animation library such as movement, transformation, and shared element transition.
</p><br>

<p align="center">
<img src="previews/preview0.gif" width="270"/>
<img src="previews/preview1.gif" width="270"/>
<img src="previews/preview2.gif" width="270"/>
</p>

## Download
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/orbitary.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22orbitary%22)

### Gradle

Add the dependency below to your **module**'s `build.gradle` file:
```gradle
dependencies {
    implementation "com.github.skydoves:orbitary:0.1.0"
}
```

> **Note**: This is an experimental library that demonstares various animations with Jetpack Compose. Please make sure that your project uses Jetpack Compose `1.3.0-alpha01`, Compose Compiler `1.2.0`, and Kotlin `1.7.0`.

## Usage

You can implement three kinds of animations with Orbitary: **Movement**, **Transformation**, and **Shared Element Transition**. 
Basically, you can run animation with `Orbitary` Composable function, which provides `OrbitaryScope` that allows you to create animations.

### Transformation

<img src="previews/preview1.gif" width="33%" align="center">


The example below shows how to implement resizing animation with the `animateTransformation` extension of the `OrbitaryScope`.
The `rememberContentWithOrbitaryScope` allows you to create custom animations such as `animateTransformation` on the `OrbitaryScope`.
You can apply the `animateTransformation` animation to specific Composables and customize its `AnimationSpec` as seen the below:

```kotlin
  val transformationSpec = SpringSpec<IntSize>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = 200f
  )

  var isTransformed by rememberSaveable { mutableStateOf(false) }
  val poster = rememberContentWithOrbitaryScope {
    GlideImage(
      modifier = if (isTransformed) {
        Modifier.size(300.dp, 620.dp)
      } else {
        Modifier.size(100.dp, 220.dp)
      }.animateTransformation(this, transformationSpec),
      imageModel = ItemUtils.urls[0],
      contentScale = ContentScale.Fit
    )
  }

  Orbitary(
    modifier = Modifier
      .clickable { isTransformed = !isTransformed }
  ) {
    Column(
      Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      poster()
    }
  }
```

### Movement

<img src="previews/preview2.gif" width="33%" align="center">

The example below shows how to implement movement animation with the `animateMovement` extension of the `OrbitaryScope`.
The `rememberContentWithOrbitaryScope` allows you to create custom animations such as `animateMovement` on the `OrbitaryScope`.
You can apply the `animateMovement` animation to specific Composables and customize its `AnimationSpec` as seen the below:

```kotlin
  val movementSpec = SpringSpec<IntOffset>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = 200f
  )
  
  var isTransformed by rememberSaveable { mutableStateOf(false) }
  val poster = rememberContentWithOrbitaryScope {
    GlideImage(
      modifier = if (isTransformed) {
        Modifier.size(360.dp, 620.dp)
      } else {
        Modifier.size(130.dp, 220.dp)
      }.animateMovement(this, movementSpec),
      imageModel = ItemUtils.urls[3],
      contentScale = ContentScale.Fit
    )
  }

  Orbitary(
    modifier = Modifier
      .clickable { isTransformed = !isTransformed }
  ) {
    if (isTransformed) {
      Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        poster()
      }
    } else {
      Column(
        Modifier
          .fillMaxSize()
          .padding(20.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
      ) {
        poster()
      }
    }
  }
```

### Shared Element Transition

<img src="previews/preview0.gif" width="33%" align="center">

The example below shows how to implement shared element transition with the `animateSharedElementTransition` extension of the `OrbitaryScope`.
The `rememberContentWithOrbitaryScope` allows you to create custom animations such as `animateSharedElementTransition` on the `OrbitaryScope`.
You can apply the `animateSharedElementTransition` animation to specific Composables and customize its `AnimationSpec`.
Also, you can set the different `AnimationSpec`s for the movement and transformation as seen the below:

```kotlin
  var isTransformed by rememberSaveable { mutableStateOf(false) }
  val items = rememberContentWithOrbitaryScope {
    ItemUtils.urls.forEach { url ->
      GlideImage(
        modifier = if (isTransformed) {
          Modifier.size(140.dp, 180.dp)
        } else {
          Modifier.size(100.dp, 220.dp)
        }
          .animateSharedElementTransition(this, movementSpec, transformationSpec)
          .padding(8.dp),
        imageModel = url,
        contentScale = ContentScale.Fit
      )
    }
  }

  Orbitary(
    modifier = Modifier
      .fillMaxSize()
      .clickable { isTransformed = !isTransformed },
    isTransformed = isTransformed,
    onStartContent = {
      Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        items()
      }
    },
    onTransformedContent = {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) { items() }
    }
  )
```


## Find this repository useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/orbitary/stargazers)__ for this repository. :star: <br>
Also, __[follow me](https://github.com/skydoves)__ on GitHub for my next creations! 🤩

# License
```xml
Designed and developed by 2022 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
