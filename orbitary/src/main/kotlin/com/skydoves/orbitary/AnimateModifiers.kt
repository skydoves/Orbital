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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Allows a custom modifier to animate the local position and size of the layout within the
 * LookaheadLayout, whenever there's a change in the layout.
 *
 * @param orbitaryScope The measurement and placement of any layout calculated in the lookahead pass can be observed via Modifier.
 * @param movementSpec An [AnimationSpec] which has [IntOffset] as a generic type.
 * @param transformSpec An [AnimationSpec] which has [IntSize] as a generic type.
 */
public fun Modifier.animateSharedElementTransition(
  orbitaryScope: OrbitaryScope,
  movementSpec: AnimationSpec<IntOffset>,
  transformSpec: AnimationSpec<IntSize>,
): Modifier {
  return this
    .then(
      animateMovement(orbitaryScope, movementSpec)
    )
    .then(
      animateTransformation(orbitaryScope, transformSpec)
    )
}
