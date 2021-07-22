
/*
 *
 *  * Copyright 2021 Manuel Wrage
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.ivianuu.playlistshuffler.api

import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import okhttp3.*
import retrofit2.*
import retrofit2.http.*

interface SpotifyApi {
  @GET("me/playlists")
  suspend fun getUsersPlaylists(
    @Query("limit") limit: Int,
    @Query("offset") offset: Int
  ): Pager<SimplePlaylist>

  @GET("playlists/{id}")
  suspend fun getPlaylist(@Path("id") id: String): Playlist

  @GET("playlists/{playlist_id}/tracks?market=from_token")
  suspend fun getPlaylistTracks(
    @Path("playlist_id") id: String,
    @Query("limit") limit: Int,
    @Query("offset") offset: Int
  ): Pager<PlaylistTrack>

  @POST("users/{user_id}/playlists")
  suspend fun createPlaylist(
    @Path("user_id") userId: String,
    @Body options: CreatePlaylistOptions
  ): Playlist

  @POST("playlists/{playlist_id}/tracks")
  suspend fun addTracksToPlaylist(
    @Path("playlist_id") playlistId: String,
    @Body tracksToAdd: TracksToAdd
  )

  @HTTP(method = "DELETE", path = "playlists/{playlist_id}/tracks", hasBody = true)
  suspend fun removeTracksFromPlaylist(
    @Path("playlist_id") playlistId: String,
    @Body tracksToRemove: TracksToRemove
  )

  @GET("me")
  suspend fun getCurrentUser(): User
}

@Provide fun spotifyApi(
  converterFactory: Converter.Factory,
  spotifyClient: SpotifyClient
): @Scoped<AppScope> SpotifyApi = Retrofit.Builder()
  .baseUrl(ApiConstants.BASE_URL)
  .callFactory { spotifyClient.newCall(it) }
  .addConverterFactory(converterFactory)
  .build()
  .create()

typealias SpotifyClient = OkHttpClient

@Provide fun spotifyClient(
  connectionPool: ConnectionPool,
  spotifyAuthenticator: SpotifyAuthenticator,
  spotifyTokenInterceptor: SpotifyTokenInterceptor,
  rateLimitInterceptor: RateLimitInterceptor
): @Scoped<AppScope> SpotifyClient = OkHttpClient.Builder()
  .authenticator(spotifyAuthenticator)
  .addInterceptor(spotifyTokenInterceptor)
  .addInterceptor(rateLimitInterceptor)
  .connectionPool(connectionPool)
  .build()
