package net.group.androidlocalmessanger.network.client.controller

import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.controller.Sender.sendRequest
import net.group.androidlocalmessanger.ui.auth.AuthViewModel

object AuthController {
    val authViewModel = AuthViewModel()

    fun authResponse(response: Response<*>, login: Boolean) {
        if (response.code == ResponseCode.OK) {
            val userWithGroups = response.data as UserWithGroups
            ClientController.client.user = userWithGroups.user
        }
        if (login)
            authViewModel.loginResponse(response as Response<UserWithGroups>)
        else
            authViewModel.registerResponse(response as Response<UserWithGroups>)

    }


    suspend fun sendLoginRequest(user: User) {
        sendRequest(OrderData(Order.Login, user))
    }

    suspend fun sendRegisterRequest(user: User) {
        sendRequest(OrderData(Order.Register, user))
    }
}