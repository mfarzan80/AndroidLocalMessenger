package net.group.androidlocalmessanger.ui.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.module.ResponseCode
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.client.controller.AuthController

class AuthViewModel : ViewModel() {
    val userState: MutableState<DataOrException<User, Boolean, ResponseCode>> =
        mutableStateOf(
            DataOrException(
                null, false, null
            )
        )

    fun register(user: User) {
        viewModelScope.launch {
            startLoading()
            AuthController.sendRegisterRequest(user)
        }
    }

    fun login(user: User) {
        viewModelScope.launch {
            startLoading()
            AuthController.sendLoginRequest(user)
        }
    }

    fun registerResponse(response: Response<User>) {
        resolveResponse(response)
    }

    fun loginResponse(response: Response<User>) {
        resolveResponse(response)
    }

    fun resolveResponse(response: Response<User>) {
        stopLoading()
        userState.value = userState.value.copy(info = response.code)
        userState.value = userState.value.copy(data = response.data)
    }

    fun startLoading() {
        userState.value = userState.value.copy(loading = true)
    }

    fun stopLoading() {
        userState.value = userState.value.copy(loading = false)
    }

    fun auth(user: User, login: Boolean) {
        if (login)
            login(user)
        else
            register(user)
    }
}