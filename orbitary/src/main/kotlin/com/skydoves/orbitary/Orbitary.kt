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

package com.skydoves.orbitary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadLayout

/**
 * Orbitary is a layout that measures and placements internally, and collocates the measured layouts
 * on the [LookaheadLayout] continuously. You can allows the [content] to run animations such as
 * [animateMovement], [animateTransformation], and [animateSharedElementTransition] on the [OrbitaryScope].
 *
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param content A Composable that receives [OrbitaryScope].
 */
@Composable
public fun Orbitary(
  modifier: Modifier = Modifier,
  content: @Composable OrbitaryScope.() -> Unit
) {
  LookaheadLayout(
    content = {
      val orbitaryScope = remember { OrbitaryScope(this) }
      orbitaryScope.content()
    },
    modifier = modifier
  ) { measurables, constraints ->
    val placeables = measurables.map { it.measure(constraints) }
    val maxWidth: Int = placeables.maxOf { it.width }
    val maxHeight = placeables.maxOf { it.height }
    layout(maxWidth, maxHeight) {
      placeables.forEach {
        it.place(0, 0)
      }
    }
  }
}

/**
 * Orbitary is a layout that measures and placements internally, and collocates the measured layouts
 * on the [LookaheadLayout] continuously. You can allows the [onStartContent] and [onTransformedContent]
 * to run animations such as [animateMovement], [animateTransformation], and [animateSharedElementTransition]
 * on the [OrbitaryScope].
 *
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param isTransformed The criteria to decide which Composable should be executed.
 * @param onStartContent A Composable that receives [OrbitaryScope]. This will be executed if the [isTransformed] is `false`.
 * @param onTransformedContent A Composable that receives [OrbitaryScope]. This will be executed if the [isTransformed] is `true`.
 */
@Composable
public fun Orbitary(
  modifier: Modifier = Modifier,
  isTransformed: Boolean,
  onStartContent: @Composable OrbitaryScope.() -> Unit,
  onTransformedContent: @Composable OrbitaryScope.() -> Unit
) {
  Orbitary(
    modifier = modifier,
    content = {
      if (isTransformed) {
        onStartContent()
      } else {
        onTransformedContent()
      }
    }
  )
}
