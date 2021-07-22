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
import com.ivianuu.injekt.scope.*
import com.jakewharton.retrofit2.converter.kotlinx.serialization.*
import kotlinx.serialization.json.*
import okhttp3.*
import retrofit2.*

@Provide fun converterFactory(json: Json): @Scoped<AppScope> Converter.Factory =
  json.asConverterFactory(MediaType.get("application/json"))

@Provide fun connectionPool(): @Scoped<AppScope> ConnectionPool = ConnectionPool()
