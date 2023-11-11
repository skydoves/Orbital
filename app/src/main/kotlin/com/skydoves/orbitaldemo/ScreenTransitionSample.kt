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
package com.skydoves.orbitaldemo

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.orbital.Orbital
import com.skydoves.orbital.animateBounds
import com.skydoves.orbital.rememberMovableContentOf

enum class Screen {
  A, B
}

@Composable
fun ScreenTransitionSample() {
  Orbital {
    var screen by rememberSaveable { mutableStateOf(Screen.A) }
    val sizeAnim = spring<IntSize>(stiffness = Spring.StiffnessLow)
    val positionAnim = spring<IntOffset>(stiffness = Spring.StiffnessLow)
    val image = rememberMovableContentOf {
      GlideImage(
        imageModel = { MockUtils.getMockPoster().poster },
        component = rememberImageComponent {
          +CrossfadePlugin()
        },
        modifier = Modifier
          .padding(10.dp)
          .animateBounds(
            modifier = if (screen == Screen.A) {
              Modifier.size(80.dp)
            } else {
              Modifier.fillMaxWidth()
            },
            sizeAnimationSpec = sizeAnim,
            positionAnimationSpec = positionAnim,
          )
          .clip(RoundedCornerShape(12.dp)),
        imageOptions = ImageOptions(requestSize = IntSize(600, 600)),
      )
    }

    val title = rememberMovableContentOf {
      Column(
        modifier = Modifier
          .padding(10.dp)
          .animateBounds(
            modifier = Modifier,
            sizeAnimationSpec = sizeAnim,
            positionAnimationSpec = positionAnim,
          ),
      ) {
        Text(
          text = MockUtils.getMockPoster().name,
          fontSize = 18.sp,
          color = Color.Black,
          fontWeight = FontWeight.Bold,
        )

        Text(
          text = MockUtils.getMockPoster().description,
          color = Color.Gray,
          fontSize = 12.sp,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis,
          fontWeight = FontWeight.Bold,
        )
      }
    }

    if (screen == Screen.A) {
      ScreenA(
        sharedContent = {
          image()
          title()
        },
      ) {
        screen = Screen.B
      }
    } else {
      ScreenB(
        sharedContent = {
          image()
          title()
        },
      ) {
        screen = Screen.A
      }
    }
  }
}

@Composable
private fun ScreenA(
  sharedContent: @Composable () -> Unit,
  navigateToScreenB: () -> Unit,
) {
  Orbital {
    Row(
      modifier = Modifier
        .background(color = Color(0xFFffd7d7))
        .fillMaxSize()
        .clickable {
          navigateToScreenB.invoke()
        },
    ) {
      sharedContent()
    }
  }
}

@Composable
private fun ScreenB(
  sharedContent: @Composable () -> Unit,
  navigateToScreenA: () -> Unit,
) {
  Orbital {
    Column(
      modifier = Modifier
        .background(color = Color(0xFFe3ffd9))
        .fillMaxSize()
        .clickable {
          navigateToScreenA()
        },
    ) {
      sharedContent()
    }
  }
}
