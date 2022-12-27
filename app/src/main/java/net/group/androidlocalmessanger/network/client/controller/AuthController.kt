package net.group.androidlocalmessanger.network.client.controller

import net.group.androidlocalmessanger.module.Order
import net.group.androidlocalmessanger.module.OrderData
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.client.controller.Sender.sendRequest
import net.group.androidlocalmessanger.ui.auth.AuthViewModel

object AuthController {
    val authViewModel = AuthViewModel()

    fun registerResponse(response: Response<*>?) {
        authViewModel.registerResponse(response!! as Response<User>)
    }

    fun loginResponse(response: Response<*>?) {
        authViewModel.loginResponse(response!! as Response<User>)
    }

    suspend fun sendLoginRequest(user: User) {
        sendRequest(OrderData(Order.Login, user))
    }

    suspend fun sendRegisterRequest(user: User) {
        sendRequest(OrderData(Order.Register, user))
    }
}