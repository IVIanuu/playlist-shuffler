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

typealias PlayShuffledPlaylistUseCase = suspend (String) -> Unit

@Provide fun playShuffledPlaylistUseCase(
  context: Context,
  mainDispatcher: MainDispatcher,
  navigator: Navigator,
  spotifyApi: SpotifyApi
): PlayShuffledPlaylistUseCase = { input ->
  println("Received $input")

  val playlistId = input.playlistId()

  // retrieve info about the playlist to play
  val playlist = spotifyApi.getPlaylist(playlistId)
  val playlistTracks = spotifyApi.getPlaylistTracksPaged(playlistId)

  println("Got info for ${playlist.name}")

  val shufflePlaylistId = collectIndexedPages {
    spotifyApi.getUsersPlaylists(50, it)
  }.firstOrNull { it.name == SHUFFLE_PLAYLIST_NAME }
    ?.also { shufflePlaylist ->
      spotifyApi.getPlaylistTracksPaged(shufflePlaylist.id)
        .mapNotNull { it.track?.id }
        .map { TrackToRemove("spotify:track:$it") }
        .chunked(100)
        .forEach { chunkedTracksToRemove ->
          spotifyApi.removeTracksFromPlaylist(
            shufflePlaylist.id,
            TracksToRemove(chunkedTracksToRemove)
          )
        }
    }
    ?.id
    ?: spotifyApi.createPlaylist(
      spotifyApi.getCurrentUser().id,
      CreatePlaylistOptions(
        name = SHUFFLE_PLAYLIST_NAME,
        public = false
      )
    ).id

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
    playerApi.play("spotify:track:02uEjuRG2GnzUVvyL0KWro").await()
    playerApi.setShuffle(false).await()
    playerApi.play("spotify:playlist:$shufflePlaylistId").await()
  }

  navigator.push(UrlKey("spotify:playlist:$shufflePlaylistId"))

  println("Launched app")
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

private const val SHUFFLE_PLAYLIST_NAME = "Playlist Shuffler"

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
}.map { block(it) }
