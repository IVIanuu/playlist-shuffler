/*
 * Copyright 2020 Manuel Wrage
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

import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.data.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import com.ivianuu.playlistshuffler.api.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide @Scoped<AppScope>
class AuthRepository(
  private val authApi: SpotifyAuthApi,
  private val authPref: DataStore<AuthPrefs>,
  private val ioDispatcher: IODispatcher
) {
  val accessToken: Flow<String?> get() = authPref.data.map { it.accessToken }

  val needsAuthentication: Flow<Boolean>
    get() = authPref
      .data
      .map { it.accessToken == null }
      .distinctUntilChanged()

  suspend fun authenticateWithCode(code: String): Result<String, Throwable> = catch {
    withContext(ioDispatcher) {
      val refreshToken = authApi.getRefreshTokenAsync(
        grantType = "authorization_code",
        code = code,
        redirectUri = ApiConstants.REDIRECT_URI,
        clientId = ApiConstants.CLIENT_ID,
        clientSecret = ApiConstants.CLIENT_SECRET
      ).refreshToken
      authPref.updateData {
        copy(refreshToken = refreshToken)
      }
      refreshAccessToken()
    }
  }

  suspend fun refreshAccessToken(): String = withContext(ioDispatcher) {
    val refreshToken = authPref.data.first().refreshToken!!
    val accessToken = authApi.refreshAccessTokenAsync(
      grantType = "refresh_token",
      refreshToken = refreshToken,
      clientId = ApiConstants.CLIENT_ID,
      clientSecret = ApiConstants.CLIENT_SECRET
    ).accessToken
    authPref.updateData { copy(accessToken = accessToken) }
    return@withContext accessToken
  }
}
