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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round
import kotlinx.coroutines.launch

/**
 * https://github.com/androidx/androidx/blob/c8d02ae9dc3baa3ad7d974da00ed08e5d00dac74/compose/ui/ui/samples/src/main/java/androidx/compose/ui/samples/LookaheadLayoutSample.kt
 *
 * Creates a custom modifier to animate the local position of the layout within the
 * LookaheadLayout, whenever there's a change in the layout.
 *
 * @param orbitaryScope The measurement and placement of any layout calculated in the lookahead pass can be observed via Modifier.
 * @param animationSpec An [AnimationSpec] which has [IntOffset] as a generic type.
 */
public fun Modifier.animateMovement(
  orbitaryScope: OrbitaryScope,
  animationSpec: AnimationSpec<IntOffset>
): Modifier = composed {
  var offsetAnimation: Animatable<IntOffset, AnimationVector2D>? by remember {
    mutableStateOf(
      null
    )
  }

  var placementOffset: IntOffset by remember { mutableStateOf(IntOffset.Zero) }
  var targetOffset: IntOffset? by remember {
    mutableStateOf(null)
  }
  // Create a `LaunchEffect` to handle target size change. This avoids creating side effects
  // from measure/layout phase.
  LaunchedEffect(Unit) {
    snapshotFlow {
      targetOffset
    }.collect { target ->
      if (target != null && target != offsetAnimation?.targetValue) {
        offsetAnimation?.run {
          launch {
            animateTo(
              targetValue = target,
              animationSpec = animationSpec
            )
          }
        } ?: Animatable(target, IntOffset.VectorConverter).let {
          offsetAnimation = it
        }
      }
    }
  }
  with(orbitaryScope) {
    this@composed
      .onPlaced { lookaheadScopeCoordinates, layoutCoordinates ->
        // This block of code has the LookaheadCoordinates of the LookaheadLayout
        // as the first parameter, and the coordinates of this modifier as the second
        // parameter.

        // localLookaheadPositionOf returns the *target* position of this
        // modifier in the LookaheadLayout's local coordinates.
        targetOffset = lookaheadScopeCoordinates
          .localLookaheadPositionOf(
            layoutCoordinates
          )
          .round()
        // localPositionOf returns the *current* position of this
        // modifier in the LookaheadLayout's local coordinates.
        placementOffset = lookaheadScopeCoordinates
          .localPositionOf(
            layoutCoordinates, Offset.Zero
          )
          .round()
      }
      // The measure logic in `intermediateLayout` is skipped in the lookahead pass, as
      // intermediateLayout is expected to produce intermediate stages of a layout
      // transform. When the measure block is invoked after lookahead pass, the lookahead
      // size of the child will be accessible as a parameter to the measure block.
      .intermediateLayout { measurable, constraints, _ ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
          // offsetAnimation will animate the target position whenever it changes.
          // In order to place the child at the animated position, we need to offset
          // the child based on the target and current position in LookaheadLayout.
          val (x, y) = offsetAnimation?.run { value - placementOffset }
            // If offsetAnimation has not been set up yet (i.e. in the first frame),
            // skip the animation
            ?: (targetOffset!! - placementOffset)
          placeable.place(x, y)
        }
      }
  }
}
