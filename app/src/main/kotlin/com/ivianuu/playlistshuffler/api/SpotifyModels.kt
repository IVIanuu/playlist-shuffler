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

package com.ivianuu.playlistshuffler.api

import kotlinx.serialization.*

@Serializable data class AccessToken(@SerialName("access_token") val accessToken: String)

@Serializable data class Image(@SerialName("url") val url: String)

@Serializable data class Pager<T>(
  @SerialName("items") val items: List<T>,
  @SerialName("total") val total: Int
)

@Serializable data class Playlist(
  @SerialName("id") val id: String,
  @SerialName("name") val name: String,
  @SerialName("images") val images: List<Image>
)

@Serializable data class PlaylistTrack(
  @SerialName("added_at") val addedAt: String?,
  @SerialName("track") val track: Track?
)

@Serializable data class Track(
  @SerialName("id") val id: String?,
  @SerialName("name") val name: String
)

@Serializable data class SimplePlaylist(
  @SerialName("id") val id: String,
  @SerialName("name") val name: String,
  @SerialName("images") val images: List<Image>
)

@Serializable data class RefreshToken(@SerialName("refresh_token") val refreshToken: String)

@Serializable data class TracksToAdd(@SerialName("uris") val uris: List<String>)

@Serializable data class TracksToRemove(@SerialName("tracks") val uris: List<TrackToRemove>)

@Serializable data class TrackToRemove(@SerialName("uri") val uri: String)

@Serializable data class CreatePlaylistOptions(
  @SerialName("name") val name: String,
  @SerialName("public") val public: Boolean
)

@Serializable data class User(@SerialName("id") val id: String)
