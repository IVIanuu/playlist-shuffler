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

package com.ivianuu.playlistshuffler.app

import androidx.compose.material.*
import com.google.accompanist.pager.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import com.ivianuu.playlistshuffler.api.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Provide object HomeKey : RootKey

@OptIn(ExperimentalPagerApi::class)
@Provide
val homeUi: ModelKeyUi<HomeKey, HomeModel> = {
  Text("Nothing to see here")
}

class HomeModel

@Provide fun homeModel(
  authRepository: AuthRepository,
  navigator: Navigator,
  scope: InjektCoroutineScope<KeyUiScope>
): @Scoped<KeyUiScope> StateFlow<HomeModel> = scope.state(HomeModel()) {
  launch {
    if (authRepository.needsAuthentication.first()) {
      navigator.push(AuthKey)
    }
  }
}
