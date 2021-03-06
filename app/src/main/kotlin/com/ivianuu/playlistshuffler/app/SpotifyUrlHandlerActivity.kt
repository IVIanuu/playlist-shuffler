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

import android.content.*
import android.os.*
import androidx.activity.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import com.ivianuu.playlistshuffler.R
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
        if (!playShuffledPlaylist(url))
          showToast(R.string.shuffling_failed, toaster, rp)
        finish()
      }
    }
  }
}

@Provide @ScopeElement<AppScope>
class SpotifyUrlHandlerActivityComponent(
  val playShuffledPlaylist: PlayShuffledPlaylistUseCase,
  val rp: ResourceProvider,
  val scope: InjektCoroutineScope<AppScope>,
  val toaster: Toaster
)
