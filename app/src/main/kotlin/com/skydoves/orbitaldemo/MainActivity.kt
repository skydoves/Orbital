/*
 * Designed and developed by 2023 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.skydoves.orbitaldemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateMovement
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.animateTransformation
import com.skydoves.orbital.rememberContentWithOrbitalScope
import com.skydoves.orbitaldemo.ui.OrbitalTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      OrbitalTheme {
        ContainerTransformDemo()
      }
    }
  }

  @Composable
  fun NavigationComposeShared() {
    SharedTransitionLayout {
      val listAnimals = remember {
        listOf(
          Animal("Lion", "", R.drawable.poster),
          Animal("Lizard", "", R.drawable.poster),
          Animal("Elephant", "", R.drawable.poster),
          Animal("Penguin", "", R.drawable.poster),
        )
      }
      val boundsTransform = { _: Rect, _: Rect -> tween<Rect>(1400) }

      val navController = rememberNavController()
      NavHost(navController = navController, startDestination = "home") {
        composable("home") {
          LazyColumn(
            modifier = Modifier
              .background(Color.Black)
              .fillMaxSize()
              .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            itemsIndexed(listAnimals) { index, item ->
              Row(
                Modifier.clickable {
                  navController.navigate("details/$index")
                },
              ) {
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                  painterResource(id = item.image),
                  contentDescription = item.description,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .size(100.dp)
                    .sharedElement(
                      rememberSharedContentState(key = "image-$index"),
                      animatedVisibilityScope = this@composable,
                      boundsTransform = boundsTransform,
                    ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  item.name,
                  fontSize = 18.sp,
                  modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .sharedElement(
                      rememberSharedContentState(key = "text-$index"),
                      animatedVisibilityScope = this@composable,
                      boundsTransform = boundsTransform,
                    ),
                )
              }
            }
          }
        }
        composable(
          "details/{animal}",
          arguments = listOf(navArgument("animal") { type = NavType.IntType }),
        ) { backStackEntry ->
          val animalId = backStackEntry.arguments?.getInt("animal")
          val animal = listAnimals[animalId!!]
          Column(
            Modifier
              .fillMaxSize()
              .background(Color.Black)
              .clickable {
                navController.navigate("home")
              },
          ) {
            Image(
              painterResource(id = animal.image),
              contentDescription = animal.description,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .sharedElement(
                  rememberSharedContentState(key = "image-$animalId"),
                  animatedVisibilityScope = this@composable,
                  boundsTransform = boundsTransform,
                ),
            )
            Text(
              animal.name,
              fontSize = 18.sp,
              modifier =
              Modifier
                .fillMaxWidth()
                .sharedElement(
                  rememberSharedContentState(key = "text-$animalId"),
                  animatedVisibilityScope = this@composable,
                  boundsTransform = boundsTransform,
                ),
            )
          }
        }
      }
    }
  }

  data class Animal(
    val name: String,
    val description: String,
    @DrawableRes val image: Int,
  )

  @Composable
  private fun OrbitalTransformationExample() {
    var isTransformed by rememberSaveable { mutableStateOf(false) }
    val poster = rememberContentWithOrbitalScope {
      GlideImage(
        modifier = if (isTransformed) {
          Modifier.size(300.dp, 620.dp)
        } else {
          Modifier.size(100.dp, 220.dp)
        }.animateTransformation(this, transformationSpec),
        imageModel = { MockUtils.getMockPoster().poster },
        imageOptions = ImageOptions(contentScale = ContentScale.Fit),
      )
    }

    Orbital(
      modifier = Modifier
        .clickable { isTransformed = !isTransformed },
    ) {
      Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        poster()
      }
    }
  }

  @Composable
  private fun OrbitalMovementExample() {
    var isTransformed by rememberSaveable { mutableStateOf(false) }
    val poster = rememberContentWithOrbitalScope {
      GlideImage(
        modifier = if (isTransformed) {
          Modifier.size(130.dp, 220.dp)
        } else {
          Modifier.size(130.dp, 220.dp)
        }.animateMovement(this, movementSpec),
        imageModel = { ItemUtils.urls[3] },
        imageOptions = ImageOptions(contentScale = ContentScale.Fit),
      )
    }

    Orbital(
      modifier = Modifier
        .clickable { isTransformed = !isTransformed },
    ) {
      if (isTransformed) {
        Column(
          Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          poster()
        }
      } else {
        Column(
          Modifier
            .fillMaxSize()
            .padding(20.dp),
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.Bottom,
        ) {
          poster()
        }
      }
    }
  }

  @Composable
  private fun OrbitalSharedElementTransitionExample() {
    var isTransformed by rememberSaveable { mutableStateOf(false) }
    val item = MockUtils.getMockPosters()[3]
    val poster = rememberContentWithOrbitalScope {
      GlideImage(
        modifier = if (isTransformed) {
          Modifier.fillMaxSize()
        } else {
          Modifier.size(130.dp, 220.dp)
        }.animateSharedElementTransition(
          this,
          SpringSpec(stiffness = 500f),
          SpringSpec(stiffness = 500f),
        ),
        imageModel = { item.poster },
        imageOptions = ImageOptions(contentScale = ContentScale.Fit),
      )
    }

    Orbital(
      modifier = Modifier
        .clickable { isTransformed = !isTransformed },
    ) {
      if (isTransformed) {
        PosterDetails(
          poster = item,
          sharedElementContent = { poster() },
          pressOnBack = {},
        )
      } else {
        Column(
          Modifier
            .fillMaxSize()
            .padding(20.dp),
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.Bottom,
        ) {
          poster()
        }
      }
    }
  }

  @Composable
  private fun OrbitalMultipleSharedElementTransitionExample() {
    var isTransformed by rememberSaveable { mutableStateOf(false) }
    val items = rememberContentWithOrbitalScope {
      MockUtils.getMockPosters().take(4).forEach { item ->
        GlideImage(
          modifier = if (isTransformed) {
            Modifier.size(140.dp, 180.dp)
          } else {
            Modifier.size(100.dp, 220.dp)
          }
            .animateSharedElementTransition(this, movementSpec, transformationSpec)
            .padding(8.dp),
          imageModel = { item.poster },
          imageOptions = ImageOptions(contentScale = ContentScale.Fit),
        )
      }
    }

    Orbital(
      modifier = Modifier
        .fillMaxSize()
        .clickable { isTransformed = !isTransformed },
      isTransformed = isTransformed,
      onStartContent = {
        Column(
          Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
        ) {
          items()
        }
      },
      onTransformedContent = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) { items() }
      },
    )
  }
}
