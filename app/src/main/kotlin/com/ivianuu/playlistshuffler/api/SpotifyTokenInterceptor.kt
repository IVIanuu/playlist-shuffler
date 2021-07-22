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

import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*

/**
 * Adds the access token to each spotify request
 */
@Provide class SpotifyTokenInterceptor(
  private val authRepository: AuthRepository
) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    val accessToken = runBlocking { authRepository.accessToken.first() }

    // add token as header
    val newRequest = request.newBuilder()
      .addHeader("Authorization", "Bearer $accessToken")
      .build()

    return chain.proceed(newRequest)
  }
}
