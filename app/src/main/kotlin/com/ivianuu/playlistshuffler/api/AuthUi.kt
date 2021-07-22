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

import androidx.activity.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import com.ivianuu.playlistshuffler.R
import com.ivianuu.playlistshuffler.api.*
import com.ivianuu.playlistshuffler.home.*
import com.spotify.sdk.android.auth.*
import kotlinx.coroutines.flow.*

object AuthKey : Key<Nothing>

@Provide val authUi: ModelKeyUi<AuthKey, AuthModel> = {
  Scaffold {
    AnimatedBox(model.authenticating) { authenticating ->
      if (authenticating) {
        CircularProgressIndicator(modifier = Modifier.center())
      } else {
        Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .fillMaxSize()
            .padding(all = 72.dp)
        ) {
          Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.h2
          )

          Spacer(Modifier.weight(1f))

          Button(
            modifier = Modifier.sizeIn(minWidth = 200.dp, minHeight = 48.dp),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.elevation(0.dp),
            onClick = model.authenticate
          ) { Text(stringResource(R.string.authenticate)) }
        }
      }
    }
  }
}

@Optics data class AuthModel(
  val authenticating: Boolean = false,
  val authenticate: () -> Unit = {}
)

@Provide fun authModel(
  activity: ComponentActivity,
  authRepository: AuthRepository,
  logger: Logger,
  navigator: Navigator,
  rp: ResourceProvider,
  scope: InjektCoroutineScope<KeyUiScope>,
  toaster: Toaster
): @Scoped<KeyUiScope> StateFlow<AuthModel> = scope.state(AuthModel()) {
  action(AuthModel.authenticate()) {
    update { copy(authenticating = true) }

    val request = AuthorizationRequest.Builder(
      ApiConstants.CLIENT_ID,
      AuthorizationResponse.Type.CODE,
      ApiConstants.REDIRECT_URI
    )
      .setScopes(ApiConstants.SCOPES)
      .build()

    val intent = AuthorizationClient.createLoginActivityIntent(activity, request)

    val response = navigator.push(DefaultIntentKey(intent))
      ?.getOrNull()
      ?.let { AuthorizationClient.getResponse(it.resultCode, it.data) }
      ?: return@action

    d {
      "on auth response ${response.code} ${response.type} ${response.error} ${response.accessToken}"
    }

    when (response.type) {
      // oh nice we got a code lets fetch a refresh token
      AuthorizationResponse.Type.CODE -> {
        authRepository.authenticateWithCode(response.code)
          .fold(
            success = {
              showToast(R.string.auth_success)
              navigator.replaceTop(HomeKey)
            },
            failure = {
              it.printStackTrace()
              update { copy(authenticating = false) }
              showToast(R.string.auth_failed)
            }
          )
      }
      // error meh..
      AuthorizationResponse.Type.ERROR -> {
        update { copy(authenticating = false) }
        showToast(R.string.auth_failed)
      }
      else -> {
        // ignore other cases
        update { copy(authenticating = false) }
      }
    }
  }
}
