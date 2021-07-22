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

import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import okhttp3.*
import kotlin.time.*

@Provide class RateLimitInterceptor(private val logger: Logger) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    var response = chain.proceed(chain.request())
    if (!response.isSuccessful && response.code() == 429) {
      val retryTimeout =
        response.header("Retry-After")?.removeSuffix("s")
          ?.toDoubleOrNull()?.seconds?.let { it + 1.seconds } ?: 5.seconds
      d { "rate limit applied $response retry after $retryTimeout" }
      try {
        Thread.sleep(retryTimeout.toLongMilliseconds())
      } catch (t: Throwable) {
      }
      response.close()
      response = chain.proceed(chain.request())
    }
    return response
  }
}
