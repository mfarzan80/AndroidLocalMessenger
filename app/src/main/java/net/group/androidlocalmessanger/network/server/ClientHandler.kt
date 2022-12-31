package net.group.androidlocalmessanger.network.server

import android.util.Log
import kotlinx.coroutines.*
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.Client
import net.group.androidlocalmessanger.network.server.controller.ServerAuthController
import net.group.androidlocalmessanger.network.server.controller.ServerGroupsController
import net.group.androidlocalmessanger.repository.GroupRepository
import net.group.androidlocalmessanger.repository.UserRepository
import java.io.*


class ClientHandler(
    val userRepository: UserRepository,
    val groupRepository: GroupRepository,
    val client: Client
) {


    lateinit var output: ObjectOutputStream
    lateinit var input: ObjectInputStream

    lateinit var groupsController: ServerGroupsController
    private val socket = client.socket

    companion object {
        const val TAG = "TcpClientHandler"

    }


    suspend fun handle() {

        val authController = ServerAuthController(this)
        groupsController = ServerGroupsController(this)

        withContext(Dispatchers.IO) {

            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    try {
                        output = ObjectOutputStream(socket.getOutputStream())
                        input = ObjectInputStream(socket.getInputStream())

                        while (true) {

                            Log.d(TAG, "handle:Reading...")
                            val orderData = input.readObject() as OrderData<*>
                            Log.d(TAG, "handle: reading...  $orderData")

                            when (orderData.order.name) {
                                Order.Register.name -> authController.register(orderData)
                                Order.Login.name -> authController.login(orderData)
                                Order.CreateGroup.name -> groupsController.addGroup(orderData)
                                Order.SendMessage.name -> groupsController.sendMessage(orderData)
                                Order.GetMyGroups.name -> groupsController.loadGroupsAndSend()
                                Order.GetMessage.name -> {}
                                Order.GetAllUsers.name -> sendUsers()
                            }

                        }
                    } catch (e: IOException) {
                        Log.e(TAG, "handle: ", e)
                        try {
                            if(client.user != null)
                                ServerService.userToClient.remove(client.user)
                            input.close()
                            output.close()
                        } catch (ex: IOException) {
                            Log.e(TAG, "handle: ", e)
                        }

                    }

                }



            }
        }

    }


    suspend fun sendResponse(response: Response<*>) {
        withContext(Dispatchers.IO) {
            output.writeObject(response)
            output.flush()
            Log.d(TAG, "TcpClientHandler: sendResponse: $response")
        }
    }

    suspend fun sendUsers() {
        val users = userRepository.getAllUsers()
        sendResponse(
            Response(
                code = ResponseCode.OK,
                responseType = ResponseType.AllUsers,
                users
            )
        )
    }
}


