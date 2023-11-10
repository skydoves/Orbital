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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember

/**
 * Remember a movable Composable content.
 *
 * @param key1 A key to re-invalidate the remembered content.
 * @param content A movable Composable content that will be remembered.
 */
@Composable
public inline fun <T> rememberMovableContentOf(
  key1: Any? = null,
  crossinline content: @Composable () -> T,
): @Composable () -> Unit {
  return remember(key1 = key1) {
    movableContentOf { content.invoke() }
  }
}
