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

object ApiConstants {
  const val BASE_URL = "https://api.spotify.com/v1/"
  const val AUTH_BASE_URL = "https://accounts.spotify.com/api/"
  const val CLIENT_ID = "9c4f3f52da1a4df49096e13379bb9533"
  const val CLIENT_SECRET = "9659d67c5ed34cb7aedfc9ca79a0f19a"
  const val REDIRECT_URI = "playlist-shuffler://callback"
  val SCOPES = arrayOf(
    "app-remote-control",
    "playlist-read-private",
    "playlist-read-collaborative",
    "playlist-modify-public",
    "playlist-modify-private"
  )
}
