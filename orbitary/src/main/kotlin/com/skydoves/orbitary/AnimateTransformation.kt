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

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize

/**
 * https://github.com/androidx/androidx/blob/c8d02ae9dc3baa3ad7d974da00ed08e5d00dac74/compose/ui/ui/samples/src/main/java/androidx/compose/ui/samples/LookaheadLayoutSample.kt
 *
 * Creates a custom modifier to animate the local size of the layout within the
 * LookaheadLayout, whenever there's a change in the layout.
 *
 * @param animationSpec An [AnimationSpec] which has [IntSize] as a generic type.
 */
context(OrbitaryScope)
public fun Modifier.animateTransformation(
  animationSpec: FiniteAnimationSpec<IntSize> = spring(
    Spring.DampingRatioNoBouncy,
    Spring.StiffnessMediumLow
  )
): Modifier = composed {
  val coroutineScope = rememberCoroutineScope()
  val sizeAnimation = remember {
    DeferredAnimation(coroutineScope, IntSize.VectorConverter)
  }
  // The measure logic in `intermediateLayout` is skipped in the lookahead pass, as
  // intermediateLayout is expected to produce intermediate stages of a layout transform.
  // When the measure block is invoked after lookahead pass, the lookahead size of the
  // child will be accessible as a parameter to the measure block.
  this.intermediateLayout { measurable, _, lookaheadSize ->
    // When layout changes, the lookahead pass will calculate a new final size for the
    // child modifier. This lookahead size can be used to animate the size
    // change, such that the animation starts from the current size and gradually
    // change towards `lookaheadSize`.
    sizeAnimation.updateTarget(lookaheadSize, animationSpec)
    // Reads the animation size if the animation is set up. Otherwise (i.e. first
    // frame), use the lookahead size without animation.
    val (width, height) = sizeAnimation.value ?: lookaheadSize
    // Creates a fixed set of constraints using the animated size and ensures the sizes are minimum zero.
    val animatedConstraints = Constraints.fixed(
      width.coerceAtLeast(0), height.coerceAtLeast(0)
    )
    // Measure child/children with animated constraints.
    val placeable = measurable.measure(animatedConstraints)
    layout(placeable.width, placeable.height) {
      placeable.place(0, 0)
    }
  }
}
