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

package com.skydoves.orbital

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LookaheadLayout
import androidx.compose.ui.layout.MeasurePolicy
import java.lang.Integer.max

/**
 * Orbital is a layout that measures and placements internally, and collocates the measured layouts
 * on the [LookaheadLayout] continuously. You can allows the [content] to run animations such as
 * [animateMovement], [animateTransformation], and [animateSharedElementTransition] on the [OrbitalScope].
 *
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param measurePolicy The function that defines the measurement and layout.
 * @param content A Composable that receives [OrbitalScope].
 */
@Composable
public fun Orbital(
  modifier: Modifier = Modifier,
  measurePolicy: MeasurePolicy = internalMeasurePolicy,
  content: @Composable OrbitalScope.() -> Unit
) {
  LookaheadLayout(
    content = {
      val orbitalScope = remember { OrbitalScope(this) }
      orbitalScope.content()
    },
    modifier = modifier,
    measurePolicy = measurePolicy
  )
}

/**
 * Orbital is a layout that measures and placements internally, and collocates the measured layouts
 * on the [LookaheadLayout] continuously. You can allows the [onStartContent] and [onTransformedContent]
 * to run animations such as [animateMovement], [animateTransformation], and [animateSharedElementTransition]
 * on the [OrbitalScope].
 *
 * @param modifier [Modifier] used to adjust the layout or drawing content.
 * @param isTransformed The criteria to decide which Composable should be executed.
 * @param measurePolicy The function that defines the measurement and layout.
 * @param onStartContent A Composable that receives [OrbitalScope]. This will be executed if the [isTransformed] is `false`.
 * @param onTransformedContent A Composable that receives [OrbitalScope]. This will be executed if the [isTransformed] is `true`.
 */
@Composable
public fun Orbital(
  modifier: Modifier = Modifier,
  isTransformed: Boolean,
  measurePolicy: MeasurePolicy = internalMeasurePolicy,
  onStartContent: @Composable OrbitalScope.() -> Unit,
  onTransformedContent: @Composable OrbitalScope.() -> Unit
) {
  Orbital(
    modifier = modifier,
    measurePolicy = measurePolicy,
    content = {
      if (isTransformed) {
        onStartContent()
      } else {
        onTransformedContent()
      }
    }
  )
}

internal val internalMeasurePolicy = MeasurePolicy { measurables, constraints ->
  val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)
  val placeables = measurables.map { it.measure(contentConstraints) }
  val maxWidth: Int = max(placeables.maxOf { it.width }, constraints.minWidth)
  val maxHeight = max(placeables.maxOf { it.height }, constraints.minHeight)
  // Position the children.
  layout(maxWidth, maxHeight) {
    placeables.forEach {
      it.place(0, 0)
    }
  }
}
