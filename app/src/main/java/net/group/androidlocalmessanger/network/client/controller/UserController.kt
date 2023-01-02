package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.controller.Sender.sendRequest
import net.group.androidlocalmessanger.network.client.controller.Sender.uploadFile
import net.group.androidlocalmessanger.ui.auth.UserViewModel
import java.io.File

object UserController {
    val userViewModel = UserViewModel()

    fun authResponse(response: Response<*>, login: Boolean) {
        if (response.code == ResponseCode.OK) {
            val userWithGroups = response.data as UserWithGroups
            ClientController.client.user = userWithGroups.user
        }
        if (login)
            userViewModel.loginResponse(response as Response<UserWithGroups>)
        else
            userViewModel.registerResponse(response as Response<UserWithGroups>)

    }

    fun updateUserResponse(response: Response<*>) {
        if (response.code == ResponseCode.OK) {
            val user = response.data as User
            ClientController.client.user = user
        }
        userViewModel.updateUserResponse(response as Response<User>)
    }


    suspend fun sendLoginRequest(user: User) {
        sendRequest(OrderData(Order.Login, user))
    }

    suspend fun sendRegisterRequest(user: User) {
        sendRequest(OrderData(Order.Register, user))
    }

    suspend fun sendUpdateUserRequest(user: User) {
        sendRequest(OrderData(Order.UpdateUser, user))
        if (user.profilePath != ClientController.client.user!!.profilePath) {
            if (user.profilePath != null) {
                sendRequest(OrderData(Order.UpdateProfile, user))
                uploadFile(user.profilePath!!)
            }
        }
    }




}