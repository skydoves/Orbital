/*
 * Designed and developed by 2022 skydoves (Jaewoong Eum)
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

package com.skydoves.orbitarydemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.orbitary.Orbitary
import com.skydoves.orbitary.animateMovement
import com.skydoves.orbitary.animateSharedElementTransition
import com.skydoves.orbitary.animateTransformation
import com.skydoves.orbitary.rememberContentWithOrbitaryScope
import com.skydoves.orbitarydemo.ui.OrbitaryTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      OrbitaryTheme {
        OrbitarySharedElementTransitionExample()
//       OrbitarTransformationExample()
//        OrbitarMovementExample()
      }
    }
  }
}

@Composable
private fun OrbitarTransformationExample() {
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
}

@Composable
private fun OrbitarMovementExample() {
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
}

@Composable
private fun OrbitarySharedElementTransitionExample() {
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
}
