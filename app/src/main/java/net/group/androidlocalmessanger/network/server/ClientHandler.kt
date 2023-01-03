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
import net.group.androidlocalmessanger.utils.Utils
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.UUID


class ClientHandler(
    val context: Context,
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

        val userController = ServerUserController(this)
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
                                Order.GetFile -> sendFile(orderData)
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


    fun receiveFile(fileName: String): File {
        val receivedFile =
            File(Utils.getCashFolder(context).absolutePath + File.separator + System.nanoTime() + fileName)
        receivedFile.createNewFile()
        Log.d(TAG, "receiveFile start: " + receivedFile.path)
        Utils.receiveFile(receivedFile, input)
        Log.d(TAG, "receiveFile Finish: " + receivedFile.path)
        Utils.saveToDownload(context, receivedFile)
        return receivedFile
    }

    suspend fun sendFile(orderData: OrderData<*>) {
        sendResponse(Response(ResponseCode.OK, ResponseType.SendingFile, null))
        val filePath = orderData.data as String
        withContext(Dispatchers.IO) {
            Utils.sendFile(File(filePath), output)
        }
    }


}


