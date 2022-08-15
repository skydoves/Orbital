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

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round

/**
 * Allows a custom modifier to animate the local position and size of the layout within the
 * LookaheadLayout, whenever there's a change in the layout.
 *
 * @param movementSpec An [FiniteAnimationSpec] which has [IntOffset] as a generic type.
 * @param transformSpec An [FiniteAnimationSpec] which has [IntSize] as a generic type.
 */
public fun Modifier.animateSharedElementTransition(
  orbitalScope: OrbitalScope,
  movementSpec: FiniteAnimationSpec<IntOffset> = spring(
    Spring.DampingRatioNoBouncy,
    Spring.StiffnessMediumLow
  ),
  transformSpec: FiniteAnimationSpec<IntSize> = spring(
    Spring.DampingRatioNoBouncy,
    Spring.StiffnessMediumLow
  ),
): Modifier = composed {
  val coroutineScope = rememberCoroutineScope()
  var placementOffset: IntOffset by remember { mutableStateOf(IntOffset.Zero) }

  val offsetAnimation = remember {
    DeferredAnimation(coroutineScope, IntOffset.VectorConverter)
  }
  val sizeAnimation = remember {
    DeferredAnimation(coroutineScope, IntSize.VectorConverter)
  }
  // The measure logic in `intermediateLayout` is skipped in the lookahead pass, as
  // intermediateLayout is expected to produce intermediate stages of a layout transform.
  // When the measure block is invoked after lookahead pass, the lookahead size of the
  // child will be accessible as a parameter to the measure block.
  with(orbitalScope) {
    this@composed
      .intermediateLayout { measurable, constraints, lookaheadSize ->
        val (width, height) = sizeAnimation.value ?: lookaheadSize
        measurable
          .measure(constraints)
          .run {
            layout(width, height) {
              place(0, 0)
            }
          }
      }
      .onPlaced { lookaheadScopeCoordinates, layoutCoordinates ->
        // This block of code has the LookaheadCoordinates of the LookaheadLayout
        // as the first parameter, and the coordinates of this modifier as the second
        // parameter.

        // localLookaheadPositionOf returns the *target* position of this
        // modifier in the LookaheadLayout's local coordinates.
        val targetOffset = lookaheadScopeCoordinates
          .localLookaheadPositionOf(
            layoutCoordinates
          )
          .round()
        offsetAnimation.updateTarget(targetOffset, movementSpec)

        // localPositionOf returns the *current* position of this
        // modifier in the LookaheadLayout's local coordinates.
        placementOffset = lookaheadScopeCoordinates
          .localPositionOf(
            layoutCoordinates, Offset.Zero
          )
          .round()
      }
      .intermediateLayout { measurable, _, lookaheadSize ->
        // When layout changes, the lookahead pass will calculate a new final size for the
        // child modifier. This lookahead size can be used to animate the size
        // change, such that the animation starts from the current size and gradually
        // change towards `lookaheadSize`.
        sizeAnimation.updateTarget(lookaheadSize, transformSpec)
        // Reads the animation size if the animation is set up. Otherwise (i.e. first
        // frame), use the lookahead size without animation.
        val (width, height) = sizeAnimation.value ?: lookaheadSize
        // Creates a fixed set of constraints using the animated size
        val animatedConstraints = Constraints.fixed(width.coerceAtLeast(0), height.coerceAtLeast(0))
        // Measure child/children with animated constraints.
        val placeable = measurable.measure(animatedConstraints)
        layout(placeable.width, placeable.height) {
          // offsetAnimation will animate the target position whenever it changes.
          // In order to place the child at the animated position, we need to offset
          // the child based on the target and current position in LookaheadLayout.
          val (x, y) = offsetAnimation.value?.let { it - placementOffset }
            // If offsetAnimation has not been set up yet (i.e. in the first frame),
            // skip the animation
            ?: (offsetAnimation.target!! - placementOffset)
          placeable.place(x, y)
        }
      }
  }
}
