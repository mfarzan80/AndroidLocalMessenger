package net.group.androidlocalmessanger.network.server

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.*
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.Client
import net.group.androidlocalmessanger.network.server.controller.ServerAuthController
import net.group.androidlocalmessanger.repository.GroupRepository
import net.group.androidlocalmessanger.repository.UserRepository
import java.io.*
import java.nio.ByteBuffer


class TcpClientHandler(
    val userRepository: UserRepository,
    val groupRepository: GroupRepository,
    val client: Client
) {


    lateinit var output: ObjectOutputStream
    lateinit var input: ObjectInputStream


    companion object {
        const val TAG = "TcpClientHandler"

    }


    suspend fun handle() {


        val authController = ServerAuthController(this)


        val socket = client.socket

        val buffer = ByteBuffer.allocate(1024)


        withContext(Dispatchers.IO) {
            try {

                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        output = ObjectOutputStream(socket.getOutputStream())
                        input = ObjectInputStream(socket.getInputStream())

                        while (true) {

                            Log.d(TAG, "handle:Reading...")
                            val orderData = input.readObject() as OrderData<*>
                            Log.d(TAG, "handle: reading...  $orderData")

                            when (orderData.order.name) {
                                Order.Register.name -> authController.register(orderData)
                                Order.Login.name -> authController.login(orderData)
                                Order.CreateGroup.name -> addGroup(orderData)
                                Order.SendMessage.name -> sendMessage(orderData)
                                Order.GetMessage.name -> {}
                                Order.GetMyGroups.name -> sendUserGroups()
                            }

                        }
                    }
                }


            } catch (e: IOException) {
                Log.e(TAG, ": ", e)
                try {

                    output.close()
                } catch (ex: IOException) {
                    Log.e(TAG, ": ", e)
                }


            }
        }
    }


    private suspend fun sendUserGroups() {
        val groups = groupRepository.getAllGroups()
        sendResponse(Response(ResponseCode.OK, ResponseTypes.AllGroups, groups))
    }


    private suspend fun sendMessage(orderData: OrderData<*>) {

    }

    private suspend fun addGroup(orderData: OrderData<*>) {
        val group = orderData.data!! as Group
        groupRepository.insertGroup(group)
    }

    suspend fun sendResponse(response: Response<*>) {
        withContext(Dispatchers.IO) {
            output.writeObject(response)
            output.flush()
            Log.d(TAG, "TcpClientHandler: sendResponse: $response")
        }
    }

}
