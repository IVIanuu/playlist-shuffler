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

@file:Suppress("ClassName", "unused")

object Build {
  const val applicationId = "com.ivianuu.playlistshuffler"
  const val compileSdk = 30
  const val minSdk = 24
  const val targetSdk = 30
  const val versionCode = 1
  const val versionName = "0.0.1"
}

object Deps {
  const val gson = "com.google.code.gson:gson:2.8.5"

  object Essentials {
    private const val version = "0.0.1-dev911"
    const val android = "com.ivianuu.essentials:essentials-android:$version"
    const val gradlePlugin = "com.ivianuu.essentials:essentials-gradle-plugin:$version"
    const val twilight = "com.ivianuu.essentials:essentials-twilight:$version"
  }

  object OkHttp {
    const val LoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.9.0"
  }

  object Retrofit {
    private const val version = "2.9.0"
    const val retrofit = "com.squareup.retrofit2:retrofit:$version"
    const val serialization =
      "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
  }

  const val spotifyAuth = "com.spotify.android:auth:1.2.5"
}
