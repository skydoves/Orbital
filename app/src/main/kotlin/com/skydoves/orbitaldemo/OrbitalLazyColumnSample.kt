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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
internal fun OrbitalLazyColumnSample() {
  val mocks = MockUtils.getMockPosters()

  Orbital {
    LazyColumn {
      items(mocks, key = { it.name }) { poster ->
        var expanded by rememberSaveable { mutableStateOf(false) }
        AnimatedVisibility(
          remember { MutableTransitionState(false) }
            .apply { targetState = true },
          enter = fadeIn(),
        ) {
          Orbital(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                expanded = !expanded
              }
              .background(color = poster.color, shape = RoundedCornerShape(10.dp)),
          ) {
            val title = rememberMovableContentOf {
              Column(
                modifier = Modifier
                  .padding(10.dp)
                  .animateBounds(Modifier),
              ) {
                Text(
                  text = poster.name,
                  fontSize = 18.sp,
                  color = Color.Black,
                  fontWeight = FontWeight.Bold,
                )

                Text(
                  text = poster.description,
                  color = Color.Gray,
                  fontSize = 12.sp,
                  maxLines = 3,
                  overflow = TextOverflow.Ellipsis,
                  fontWeight = FontWeight.Bold,
                )
              }
            }
            val image = rememberMovableContentOf {
              GlideImage(
                imageModel = { poster.poster },
                component = rememberImageComponent {
                  +CrossfadePlugin()
                },
                modifier = Modifier
                  .padding(10.dp)
                  .animateBounds(
                    if (expanded) {
                      Modifier.fillMaxWidth()
                    } else {
                      Modifier.size(80.dp)
                    },
                    spring(stiffness = Spring.StiffnessLow),
                  )
                  .clip(RoundedCornerShape(5.dp)),
                imageOptions = ImageOptions(requestSize = IntSize(600, 600)),
              )
            }

            if (expanded) {
              Column {
                image()
                title()
              }
            } else {
              Row {
                image()
                title()
              }
            }
          }
        }
      }
    }
  }
}
