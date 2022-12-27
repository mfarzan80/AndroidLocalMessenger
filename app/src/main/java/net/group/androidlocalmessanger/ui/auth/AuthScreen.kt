package net.group.androidlocalmessanger.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.LoadingErrorDataView
import net.group.androidlocalmessanger.ui.component.OutlinedInput
import net.group.androidlocalmessanger.ui.component.VSpacer
import net.group.androidlocalmessanger.ui.navigation.Screen

@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel) {
    val user = authViewModel.userState.value
    val login = remember { mutableStateOf(false) }
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Log.d("Screen", "AuthScreen: ")
    ActivityView {

        Column(modifier = Modifier.padding(30.dp)) {
            if (!login.value) {
                OutlinedInput(
                    valueState = name,
                    label = "Name"
                )
                VSpacer()
            }

            OutlinedInput(
                valueState = email,
                label = "Email"
            )

            VSpacer()

            OutlinedInput(
                valueState = password,
                label = "Password",
            )

            VSpacer(20.dp)

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                authViewModel.auth(
                    User(name = name.value, email = email.value, password = password.value),
                    login.value
                )
            }) {
                Text(if (login.value) "Login" else "Register")
            }

            VSpacer(10.dp)

            OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                login.value = !login.value
            }) {
                Text(if (!login.value) "Login" else "Register")
            }

            LoadingErrorDataView(dataOrException = user) {
                LaunchedEffect(key1 = true) {
                    navController.popBackStack()
                    navController.navigate(Screen.MainScreen.name)
                }
            }

        }

    }
}