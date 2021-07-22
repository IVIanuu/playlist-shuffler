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

import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import okhttp3.*

/**
 * Automatically refreshes the token for spotify endpoints
 */
@Provide class SpotifyAuthenticator(
  private val authRepository: AuthRepository,
  private val logger: Logger
) : Authenticator {
  override fun authenticate(route: Route?, response: Response): Request? {
    if (response.getResponseCount() >= MAX_RETRIES) {
      d { "give up authenticating to many errors" }
      return null // give up.
    }

    // get new tokens
    val accessToken = runBlocking {
      catch {
        authRepository.refreshAccessToken()
      }
    }
      .onSuccess { d { "re authenticated $it" } }
      .onFailure {
        d { "couldn't re authenticate" }
        it.printStackTrace()
        return null
      }

    return response.request().newBuilder()
      .header("Authorization", "Bearer $accessToken")
      .build()
  }

  private fun Response?.getResponseCount(): Int {
    var response = this
    var result = 1
    while (response?.priorResponse() != null) {
      result++
      response = response.priorResponse()
    }
    return result
  }

  private companion object {
    private const val MAX_RETRIES = 3
  }
}
