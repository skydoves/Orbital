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
import androidx.compose.ui.UiComposable
import androidx.compose.ui.layout.LookaheadScope

/**
 * OrbitalScope is a scope that wraps [LookaheadScope] to apply animations.
 */
public class OrbitalScope internal constructor(lookaheadScope: LookaheadScope) :
  LookaheadScope by lookaheadScope

/**
 * OrbitalScope starts a scope in which all layouts scope will receive a lookahead pass preceding
 * the main measure/layout pass. This lookahead pass will calculate the layout size and position
 * for all child layouts, and make the lookahead results available in Modifier.intermediateLayout.
 *
 * Modifier.intermediateLayout gets invoked in the main pass to allow transient layout changes
 * in the main pass that gradually morph the layout over the course of multiple frames until it
 * catches up with lookahead.
 *
 * @param content - The child composable to be laid out.
 */
@UiComposable
@Composable
public fun OrbitalScope(
  content:
  @Composable @UiComposable
  OrbitalScope.() -> Unit,
) {
  LookaheadScope {
    content.invoke(OrbitalScope(this))
  }
}
