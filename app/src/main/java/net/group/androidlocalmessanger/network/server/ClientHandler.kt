package net.group.androidlocalmessanger.network.server

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.Client
import net.group.androidlocalmessanger.network.server.controller.ServerGroupsController
import net.group.androidlocalmessanger.network.server.controller.ServerUserController
import net.group.androidlocalmessanger.repository.GroupRepository
import net.group.androidlocalmessanger.repository.UserRepository
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


class ClientHandler(
    val context: Context,
    val userRepository: UserRepository,
    val groupRepository: GroupRepository,
    val client: Client,
    val fileHandler: FileHandler
) {

    lateinit var output: ObjectOutputStream
    lateinit var input: ObjectInputStream
    lateinit var groupsController: ServerGroupsController
    lateinit var userController: ServerUserController
    private val socket = client.socket

    companion object {
        const val TAG = "TcpClientHandler"

    }


    suspend fun handle() {

        userController = ServerUserController(this)
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

                            when (orderData.order) {
                                Order.Register -> userController.register(orderData)
                                Order.Login -> userController.login(orderData)
                                Order.UpdateUser -> userController.updateUser(orderData)
                                Order.UpdateProfile -> userController.updateUserProfile(orderData)
                                Order.CreateGroup -> groupsController.addGroup(orderData)
                                Order.SendMessage -> groupsController.sendMessage(orderData)
                                Order.UpdateGroup -> groupsController.updateGroupWithUsers(orderData)
                                Order.GetFile -> fileHandler.sendFile(context, orderData)
                            }

                        }
                    } catch (e: IOException) {
                        Log.e(TAG, "handle: ", e)
                        try {
                            if (client.user != null)
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
            Log.d(TAG, "sendResponse: $response")
        }
    }


    suspend fun receiveFile(fileName: String) {
        fileHandler.receiveFile(context, fileName)
    }

}


