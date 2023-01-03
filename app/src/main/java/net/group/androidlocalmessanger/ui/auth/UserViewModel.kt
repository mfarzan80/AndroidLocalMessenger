package net.group.androidlocalmessanger.ui.auth

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.module.ResponseCode
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.module.UserWithGroups
import net.group.androidlocalmessanger.network.client.controller.UserController

class UserViewModel : ViewModel() {
    val userState: MutableState<DataOrException<UserWithGroups, Boolean, ResponseCode>> =
        mutableStateOf(
            DataOrException(
                null, false, null
            )
        )

    val updatedUserState: MutableState<DataOrException<User, Boolean, ResponseCode>> =
        mutableStateOf(
            DataOrException(
                null, false, null
            )
        )

    fun resetUpdatedUser() {
        updatedUserState.value = DataOrException(null, false, null)
    }

    private fun register(user: User) {
        viewModelScope.launch {
            startLoading()
            UserController.sendRegisterRequest(user)
        }
    }

    private fun login(user: User) {
        viewModelScope.launch {
            startLoading()
            UserController.sendLoginRequest(user)
        }
    }

    fun updateUser(context: Context, user: User) {
        viewModelScope.launch {
            startLoading(true)
            UserController.sendUpdateUserRequest(context,user)
        }
    }

    fun updateUserResponse(response: Response<User>) {
        stopLoading(true)
        updatedUserState.value = updatedUserState.value.copy(info = response.code)
        if (response.code == ResponseCode.OK) {
            setUpdatedUser(response.data!!)
        }
    }

    fun setUpdatedUser(user: User, finishSetting: Boolean = true) {
        if (finishSetting)
            updatedUserState.value = updatedUserState.value.copy(data = user)
        userState.value =
            userState.value.copy(data = userState.value.data!!.copy(user = user))
    }

    fun registerResponse(response: Response<UserWithGroups>) {
        resolveResponse(response)
    }

    fun loginResponse(response: Response<UserWithGroups>) {
        resolveResponse(response)
    }

    private fun resolveResponse(response: Response<UserWithGroups>) {
        stopLoading()
        userState.value = userState.value.copy(info = response.code)
        userState.value = userState.value.copy(data = response.data)
    }

    private fun startLoading(updatedUser: Boolean = false) {
        if (updatedUser)
            updatedUserState.value = updatedUserState.value.copy(loading = true)
        else
            updatedUserState.value = updatedUserState.value.copy(loading = true)
    }

    private fun stopLoading(updatedUser: Boolean = false) {
        if (updatedUser)
            updatedUserState.value = updatedUserState.value.copy(loading = false)
        else
            updatedUserState.value = updatedUserState.value.copy(loading = false)
    }

    fun auth(user: User, login: Boolean) {
        if (login)
            login(user)
        else
            register(user)
    }


}