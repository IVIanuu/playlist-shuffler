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
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.playlistshuffler.api.*
import com.spotify.android.appremote.api.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

typealias PlayShuffledPlaylistUseCase = suspend (String) -> Boolean

@Provide fun playShuffledPlaylistUseCase(
  context: Context,
  mainDispatcher: MainDispatcher,
  navigator: Navigator,
  spotifyApi: SpotifyApi
): PlayShuffledPlaylistUseCase = { input ->
  catch {
    println("Received $input")

    val playlistId = input.playlistId()

    // retrieve info about the playlist to play
    val playlist = spotifyApi.getPlaylist(playlistId)
    val playlistTracks = spotifyApi.getPlaylistTracksPaged(playlistId)

    println("Got info for ${playlist.name}")

    val shufflePlaylistId = spotifyApi.createPlaylist(
      spotifyApi.getCurrentUser().id,
      CreatePlaylistOptions(
        name = "${playlist.name} (Shuffled)",
        public = false
      )
    ).id

    spotifyApi.unfollowPlaylist(shufflePlaylistId)

    playlistTracks
      .shuffled()
      .mapNotNull { it.track?.id }
      .map { "spotify:track:$it" }
      .chunked(100)
      .forEach { chunkedUris ->
        spotifyApi.addTracksToPlaylist(
          shufflePlaylistId,
          TracksToAdd(chunkedUris)
        )
      }

    println("Added tracks")

    withAppRemote {
      playerApi.setShuffle(false).await()
      playerApi.play("spotify:playlist:$shufflePlaylistId").await()
    }

    navigator.push(UrlKey("spotify:playlist:$shufflePlaylistId"))

    println("Launched app")
    true
  }
    .onFailure { it.printStackTrace() }
    .mapError { false }
    .get()!!
}

private fun String.playlistId() =
  substringAfter("/playlist/")
    .substringBefore("?")

private inline fun <I> collectIndexedPages(
  startOffset: Int = 0,
  fetchPage: (Int) -> Pager<I>
): List<I> {
  var offset = startOffset
  val allItems = mutableListOf<I>()
  while (true) {
    val pager = fetchPage(offset)
    allItems += pager.items
    offset += pager.items.size
    if (allItems.size >= pager.total) break
  }

  return allItems
}

private suspend fun SpotifyApi.getPlaylistTracksPaged(id: String): List<PlaylistTrack> =
  collectIndexedPages { getPlaylistTracks(id, 100, it) }

private suspend fun <R> withAppRemote(
  @Inject context: Context,
  @Inject mainDispatcher: MainDispatcher,
  block: suspend SpotifyAppRemote.() -> R
): Result<R, Throwable> = withContext(mainDispatcher) {
  suspendCancellableCoroutine<Result<SpotifyAppRemote, Throwable>> { cont ->
    SpotifyAppRemote.connect(
      context,
      ConnectionParams.Builder(ApiConstants.CLIENT_ID)
        .setRedirectUri(ApiConstants.REDIRECT_URI)
        .build(),
      object : Connector.ConnectionListener {
        override fun onConnected(appRemote: SpotifyAppRemote) {
          cont.resume(appRemote.ok())
        }

        override fun onFailure(throwable: Throwable) {
          throwable.printStackTrace()
          cont.resume(throwable.err())
        }
      }
    )
  }
}.map { appRemote ->
  try {
    block(appRemote)
  } finally {
    SpotifyAppRemote.disconnect(appRemote)
  }
}
