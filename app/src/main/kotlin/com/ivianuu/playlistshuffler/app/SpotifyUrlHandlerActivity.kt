package com.ivianuu.playlistshuffler.app

import android.content.*
import android.os.*
import androidx.activity.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*

class SpotifyUrlHandlerActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val url = intent?.dataString
      ?: intent.getStringExtra(Intent.EXTRA_TEXT)
      ?: run {
        finish()
        return
      }
    activityScope.element<SpotifyUrlHandlerActivityComponent>().run {
      scope.launch {
        playShuffledPlaylist(url)
        finish()
      }
    }
  }
}

@Provide @ScopeElement<AppScope>
class SpotifyUrlHandlerActivityComponent(
  val playShuffledPlaylist: PlayShuffledPlaylistUseCase,
  val scope: InjektCoroutineScope<AppScope>
)
