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

import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import okhttp3.*
import retrofit2.*
import retrofit2.http.*

interface SpotifyAuthApi {
  @FormUrlEncoded
  @POST("token")
  suspend fun getRefreshTokenAsync(
    @Field("grant_type") grantType: String,
    @Field("code") code: String,
    @Field("redirect_uri") redirectUri: String,
    @Field("client_id") clientId: String,
    @Field("client_secret") clientSecret: String
  ): RefreshToken

  @FormUrlEncoded
  @POST("token")
  suspend fun refreshAccessTokenAsync(
    @Field("grant_type") grantType: String,
    @Field("refresh_token") refreshToken: String,
    @Field("client_id") clientId: String,
    @Field("client_secret") clientSecret: String
  ): AccessToken
}

@Provide fun spotifyAuthApi(
  client: () -> SpotifyAuthClient,
  converterFactory: Converter.Factory,
): @Scoped<AppScope> SpotifyAuthApi = Retrofit.Builder()
  .baseUrl(ApiConstants.AUTH_BASE_URL)
  .callFactory { client().newCall(it) }
  .addConverterFactory(converterFactory)
  .build()
  .create()

typealias SpotifyAuthClient = OkHttpClient

@Provide
fun spotifyAuthClient(connectionPool: ConnectionPool): @Scoped<AppScope> SpotifyAuthClient =
  OkHttpClient.Builder()
    .connectionPool(connectionPool)
    .build()
