package net.group.androidlocalmessanger.network.server.controller

import android.util.Log
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.server.ClientHandler
import net.group.androidlocalmessanger.network.server.ClientHandler.Companion.TAG
import net.group.androidlocalmessanger.network.server.ServerService

class ServerUserController(private val clientHandler: ClientHandler) {


    suspend fun login(orderData: OrderData<*>) {

        val requestUser = orderData.data!! as User
        val userWithGroups =
            clientHandler.userRepository.getUserByEmail(email = requestUser.userEmail)

        clientHandler.sendResponse(
            if (userWithGroups == null)
                Response(ResponseCode.EMAIL_NOT_FOUND, ResponseType.Login)
            else if (userWithGroups.user.password != requestUser.password)
                Response(ResponseCode.PASSWORD_INCORRECT, ResponseType.Login)
            else {
                setClientUser(user = userWithGroups.user)
                Response<UserWithGroups?>(ResponseCode.OK, ResponseType.Login, userWithGroups)
            }
        )


        sendUsers()
        clientHandler.groupsController.loadGroupsAndSend()
    }

    private suspend fun sendUsersToAll() {
        val users = clientHandler.userRepository.getAllUsers()
        ServerService.userToClient.values.forEach { clientHandler ->
            clientHandler.userController.sendUsers(users)
        }
    }

    suspend fun register(orderData: OrderData<*>) {

        val user = orderData.data!! as User
        Log.d(TAG, "register: $user")
        val userWithGroups =
            clientHandler.userRepository.getUserByEmail(email = user.userEmail)
        val response =
            if (userWithGroups == null)
                Response(
                    ResponseCode.OK, ResponseType.Register, UserWithGroups(user, listOf())
                )
            else
                Response(
                    ResponseCode.EMAIL_EXIST, ResponseType.Register, null
                )


        clientHandler.sendResponse(response)

        if (response.code == ResponseCode.OK) {
            setClientUser(user)
            clientHandler.userRepository.insertUser(user)
            Log.d(TAG, "register:insertUser $user")
        }


        sendUsersToAll()
        clientHandler.groupsController.loadGroupsAndSend()
    }

    suspend fun updateUser(orderData: OrderData<*>) {
        val updatedUser = orderData.data as User
        val response = Response<Any?>(responseType = ResponseType.UpdatedUser)

        val currentUser = clientHandler.userRepository.getUserByEmail(updatedUser.userEmail)
        if (updatedUser.userName != null && updatedUser.userName != currentUser!!.user.userName) {
            val userExist =
                clientHandler.userRepository.getUserByUsername(updatedUser.userName!!) != null
            if (userExist) {
                response.code = ResponseCode.USER_NAME_EXIST
            } else
                response.code = ResponseCode.OK
        } else
            response.code = ResponseCode.OK


        if (response.code == ResponseCode.OK) {
            response.data = updatedUser
            setUpdatedUser(updatedUser)
        } else
            response.data = currentUser
        clientHandler.sendResponse(response)
    }

    suspend fun updateUserProfile(orderData: OrderData<*>) {
        val user = orderData.data as User
        Log.d(TAG, "updateUserProfile: ${user.profilePath}")
        clientHandler.receiveFile(user.profilePath!!)
        setUpdatedUser(user)
    }


    suspend fun sendUsers() {
        val users = clientHandler.userRepository.getAllUsers()
        sendUsers(users)
    }

    suspend fun sendUsers(users: List<User>) {
        clientHandler.sendResponse(
            Response(
                code = ResponseCode.OK,
                responseType = ResponseType.AllUsers,
                users
            )
        )
    }

    private suspend fun setUpdatedUser(updatedUser: User) {
        clientHandler.userRepository.updateUser(updatedUser)
        setClientUser(updatedUser)
    }

    private fun setClientUser(user: User) {
        clientHandler.client.user = user
        ServerService.userToClient[user] = clientHandler
    }


}