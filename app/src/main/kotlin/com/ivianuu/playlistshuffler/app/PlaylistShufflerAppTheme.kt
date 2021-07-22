/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.playlistshuffler.app

import androidx.compose.material.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import com.ivianuu.essentials.twilight.domain.*
import com.ivianuu.essentials.twilight.ui.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.material.colors
import com.ivianuu.essentials.ui.material.editEach
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

object ReleaseTrackerTheme {
  val Primary = Color(0xFF7E3ff2)
}

@Provide fun releaseTrackerTheme(twilightState: StateFlow<TwilightState>): AppTheme = { content ->
  TwilightTheme(
    lightColors = colors(
      isLight = true,
      primary = ReleaseTrackerTheme.Primary,
      primaryVariant = ReleaseTrackerTheme.Primary,
      secondary = ReleaseTrackerTheme.Primary,
      secondaryVariant = ReleaseTrackerTheme.Primary
    ),
    darkColors = colors(
      isLight = false,
      primary = ReleaseTrackerTheme.Primary,
      primaryVariant = ReleaseTrackerTheme.Primary,
      secondary = ReleaseTrackerTheme.Primary,
      secondaryVariant = ReleaseTrackerTheme.Primary
    ),
    blackColors = colors(
      isLight = false,
      primary = ReleaseTrackerTheme.Primary,
      primaryVariant = ReleaseTrackerTheme.Primary,
      secondary = ReleaseTrackerTheme.Primary,
      secondaryVariant = ReleaseTrackerTheme.Primary,
      background = Color.Black,
      surface = Color.Black
    ),
    typography = Typography().editEach {
      copy()//copy(fontFamily = font(R.font.roboto_mono).asFontFamily())
    },
    twilightState = twilightState.collectAsState().value
  ) {
    CompositionLocalProvider(
      LocalStackTransition provides ScaledSharedAxisStackTransition(),
      content = content
    )
  }
}