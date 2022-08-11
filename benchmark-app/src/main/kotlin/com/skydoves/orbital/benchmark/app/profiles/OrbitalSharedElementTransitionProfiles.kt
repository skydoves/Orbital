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

package com.skydoves.orbital.benchmark.app.profiles

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateSharedElementTransition
import com.skydoves.orbital.rememberContentWithOrbitalScope

@Composable
fun OrbitalSharedElementTransitionProfiles() {
  var isTransformed by rememberSaveable { mutableStateOf(false) }
  val item = rememberContentWithOrbitalScope {
    Box(
      modifier = if (isTransformed) {
        Modifier.fillMaxSize()
      } else {
        Modifier.size(60.dp, 60.dp)
      }
        .animateSharedElementTransition(
          SpringSpec(stiffness = 500f),
          SpringSpec(stiffness = 500f)
        )
        .background(Color.Yellow)
    )
  }

  Orbital(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { isTransformed = !isTransformed }
  ) {
    if (isTransformed) {
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(90.dp, 90.dp)
            .background(Color.Blue)
        )
      }
    } else {
      Column(
        Modifier.padding(20.dp),
      ) {
        item()
      }
    }
  }
}
