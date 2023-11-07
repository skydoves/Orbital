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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class DeferredAnimation<T, V : AnimationVector>(
  coroutineScope: CoroutineScope,
  vectorConverter: TwoWayConverter<T, V>
) {
  val value: T?
    get() = animatable?.value ?: target
  var target: T? by mutableStateOf(null)
    private set
  private var animationSpec: FiniteAnimationSpec<T> = spring()
  private var animatable: Animatable<T, V>? = null

  init {
    coroutineScope.launch {
      snapshotFlow { target }.collect { target ->
        if (target != null && target != animatable?.targetValue) {
          animatable?.run {
            launch { animateTo(target, animationSpec) }
          } ?: Animatable(target, vectorConverter).let {
            animatable = it
          }
        }
      }
    }
  }

  fun updateTarget(targetValue: T, animationSpec: FiniteAnimationSpec<T>) {
    target = targetValue
    this.animationSpec = animationSpec
  }
}
