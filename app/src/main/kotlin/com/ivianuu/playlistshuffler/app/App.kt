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

@file:Providers(
  "com.ivianuu.essentials.*",
  "com.ivianuu.essentials.app.*",
  "com.ivianuu.essentials.data.*",
  "com.ivianuu.essentials.logging.*",
  "com.ivianuu.essentials.logging.android.*",
  "com.ivianuu.essentials.serialization.*",
  "com.ivianuu.essentials.twilight.data.*",
  "com.ivianuu.essentials.twilight.domain.*",
  "com.ivianuu.essentials.twilight.ui.*",
  "com.ivianuu.essentials.ui.*",
  "com.ivianuu.essentials.ui.core.*",
  "com.ivianuu.essentials.ui.navigation.*",
  "com.ivianuu.essentials.util.*",
  "com.ivianuu.injekt.android.*",
  "com.ivianuu.injekt.coroutines.*",
  "com.ivianuu.injekt.scope.*",
  "com.ivianuu.playlistshuffler.api.*",
  "com.ivianuu.playlistshuffler.home.*",
  "com.ivianuu.playlistshuffler.network.*"
)

package com.ivianuu.playlistshuffler.app

import com.ivianuu.essentials.app.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

class App : EsApp() {
  override fun buildAppScope(): AppScope = createAppScope()
}
