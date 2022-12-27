package net.group.androidlocalmessanger.network.server.controller

import android.util.Log
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.server.TcpClientHandler
import net.group.androidlocalmessanger.network.server.TcpClientHandler.Companion.TAG

class ServerAuthController(private val tcpClientHandler: TcpClientHandler) {


    suspend fun login(orderData: OrderData<*>) {

        val requestUser = orderData.data!! as User
        val serverUser =
            tcpClientHandler.userRepository.getUserByUsername(userName = requestUser.email)

        tcpClientHandler.sendResponse(
            if (serverUser == null)
                Response(ResponseCode.USER_NAME_NOT_FOUND, ResponseTypes.Login)
            else if (serverUser.password != requestUser.password)
                Response(ResponseCode.PASSWORD_INCORRECT, ResponseTypes.Login)
            else {
                tcpClientHandler.client.user = serverUser
                Response<User?>(ResponseCode.OK, ResponseTypes.Login, serverUser)
            }
        )


    }

    suspend fun register(orderData: OrderData<*>) {

        val user = orderData.data!! as User
        Log.d(TAG, "register: $user")
        val userExist =
            tcpClientHandler.userRepository.getUserByUsername(userName = user.email) != null
        val response =
            if (!userExist)
                Response(
                    ResponseCode.OK, ResponseTypes.Register, user
                )
            else
                Response(
                    ResponseCode.USER_EXIST, ResponseTypes.Register, null
                )


        tcpClientHandler.sendResponse(response)

        if (response.code == ResponseCode.OK) {
            tcpClientHandler.client.user = user
            tcpClientHandler.userRepository.insertUser(user)
            Log.d(TAG, "register:insertUser $user")
        }

    }

}