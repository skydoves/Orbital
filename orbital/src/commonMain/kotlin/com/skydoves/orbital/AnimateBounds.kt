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
package com.skydoves.orbital

import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.intermediateLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round

/**
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/animation/animation/integration-tests/animation-demos/src/main/java/androidx/compose/animation/demos/lookahead/AnimateBoundsModifier.kt
 *
 * Creates a custom modifier to animate the bounds of the layout within the
 * LookaheadLayout, whenever there's a change in the layout.
 */
public fun Modifier.animateBounds(
  modifier: Modifier = Modifier,
  sizeAnimationSpec: FiniteAnimationSpec<IntSize> = spring(
    Spring.DampingRatioNoBouncy,
    Spring.StiffnessMediumLow,
  ),
  positionAnimationSpec: FiniteAnimationSpec<IntOffset> = spring(
    Spring.DampingRatioNoBouncy,
    Spring.StiffnessMediumLow,
  ),
  debug: Boolean = false,
  orbitalScope: (orbitalScope: OrbitalScope) -> OrbitalScope = { it },
): Modifier = composed {
  val coroutineScope = rememberCoroutineScope()
  val outerOffsetAnimation =
    remember { DeferredAnimation(coroutineScope, IntOffset.VectorConverter) }
  val outerSizeAnimation = remember { DeferredAnimation(coroutineScope, IntSize.VectorConverter) }

  val offsetAnimation = remember { DeferredAnimation(coroutineScope, IntOffset.VectorConverter) }
  val sizeAnimation = remember { DeferredAnimation(coroutineScope, IntSize.VectorConverter) }

  // The measure logic in `intermediateLayout` is skipped in the lookahead pass, as
  // intermediateLayout is expected to produce intermediate stages of a layout transform.
  // When the measure block is invoked after lookahead pass, the lookahead size of the
  // child will be accessible as a parameter to the measure block.
  this
    .drawWithContent {
      drawContent()
      if (debug) {
        val offset = outerOffsetAnimation.target!! - outerOffsetAnimation.value!!
        translate(
          offset.x.toFloat(),
          offset.y.toFloat(),
        ) {
          drawRect(Color.Black.copy(alpha = 0.5f), style = Stroke(10f))
        }
      }
    }
    .intermediateLayout { measurable, constraints ->
      val (w, h) = outerSizeAnimation.updateTarget(
        lookaheadSize,
        sizeAnimationSpec,
      )
      measurable
        .measure(constraints)
        .run {
          layout(w, h) {
            val (x, y) = outerOffsetAnimation.updateTargetBasedOnCoordinates(
              orbitalScope = OrbitalScope(this@intermediateLayout),
              placeableScope = this,
              animationSpec = positionAnimationSpec,
            )
            place(x, y)
          }
        }
    }
    .then(modifier)
    .drawWithContent {
      drawContent()
      if (debug) {
        val offset = offsetAnimation.target!! - offsetAnimation.value!!
        translate(
          offset.x.toFloat(),
          offset.y.toFloat(),
        ) {
          drawRect(Color.Green.copy(alpha = 0.5f), style = Stroke(10f))
        }
      }
    }
    .intermediateLayout { measurable, _ ->
      // When layout changes, the lookahead pass will calculate a new final size for the
      // child modifier. This lookahead size can be used to animate the size
      // change, such that the animation starts from the current size and gradually
      // change towards `lookaheadSize`.
      val (width, height) = sizeAnimation.updateTarget(
        lookaheadSize,
        sizeAnimationSpec,
      )
      // Creates a fixed set of constraints using the animated size
      val animatedConstraints = Constraints.fixed(width, height)
      // Measure child/children with animated constraints.
      val placeable = measurable.measure(animatedConstraints)
      layout(placeable.width, placeable.height) {
        val (x, y) = with(orbitalScope(OrbitalScope(this@intermediateLayout))) {
          offsetAnimation.updateTargetBasedOnCoordinates(
            orbitalScope = OrbitalScope(this@intermediateLayout),
            placeableScope = this@layout,
            animationSpec = positionAnimationSpec,
          )
        }
        placeable.place(x, y)
      }
    }
}

internal fun DeferredAnimation<IntOffset, AnimationVector2D>.updateTargetBasedOnCoordinates(
  orbitalScope: OrbitalScope,
  placeableScope: Placeable.PlacementScope,
  animationSpec: FiniteAnimationSpec<IntOffset>,
): IntOffset {
  placeableScope.coordinates?.let { coordinates ->
    with(this@updateTargetBasedOnCoordinates) {
      val targetOffset = with(orbitalScope.lookaheadScope) {
        placeableScope.lookaheadScopeCoordinates.localLookaheadPositionOf(coordinates)
      }
      val animOffset = updateTarget(
        targetOffset.round(),
        animationSpec,
      )
      val current = with(orbitalScope.lookaheadScope) {
        placeableScope.lookaheadScopeCoordinates.localPositionOf(
          coordinates,
          Offset.Zero,
        ).round()
      }
      return (animOffset - current)
    }
  }

  return IntOffset.Zero
}
