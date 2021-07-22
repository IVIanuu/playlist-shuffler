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