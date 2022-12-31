package net.group.androidlocalmessanger.network.server.controller

import android.util.Log
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.server.ClientHandler
import net.group.androidlocalmessanger.network.server.ClientHandler.Companion.TAG
import net.group.androidlocalmessanger.network.server.ServerService

class ServerAuthController(private val clientHandler: ClientHandler) {


    suspend fun login(orderData: OrderData<*>) {

        val requestUser = orderData.data!! as User
        val userWithGroups =
            clientHandler.userRepository.getUserByUsername(userName = requestUser.userEmail)

        clientHandler.sendResponse(
            if (userWithGroups == null)
                Response(ResponseCode.USER_NAME_NOT_FOUND, ResponseType.Login)
            else if (userWithGroups.user.password != requestUser.password)
                Response(ResponseCode.PASSWORD_INCORRECT, ResponseType.Login)
            else {
                setClientUser(user = userWithGroups.user)
                Response<UserWithGroups?>(ResponseCode.OK, ResponseType.Login, userWithGroups)
            }
        )


    }

    suspend fun register(orderData: OrderData<*>) {

        val user = orderData.data!! as User
        Log.d(TAG, "register: $user")
        val userWithGroups =
            clientHandler.userRepository.getUserByUsername(userName = user.userEmail)
        val response =
            if (userWithGroups == null)
                Response(
                    ResponseCode.OK, ResponseType.Register, UserWithGroups(user, listOf())
                )
            else
                Response(
                    ResponseCode.USER_EXIST, ResponseType.Register, null
                )


        clientHandler.sendResponse(response)

        if (response.code == ResponseCode.OK) {
            setClientUser(user)
            clientHandler.userRepository.insertUser(user)
            Log.d(TAG, "register:insertUser $user")
        }

    }

    private fun setClientUser(user: User) {
        clientHandler.client.user = user
        ServerService.userToClient[user] = clientHandler
    }

}