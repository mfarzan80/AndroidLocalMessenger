package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.ResponseCode
import net.group.androidlocalmessanger.network.client.Client
import net.group.androidlocalmessanger.network.server.ServerService.Companion.FT_PORT

import net.group.androidlocalmessanger.network.server.ServerService.Companion.PORT
import net.group.androidlocalmessanger.utils.Utils
import net.group.androidlocalmessanger.utils.Utils.sendFile
import java.io.*
import java.net.Socket

object ClientController {

    const val TAG = "ClientController"

    lateinit var client: Client
    lateinit var input: ObjectInputStream
    lateinit var output: ObjectOutputStream

    lateinit var fOutput: ObjectOutputStream
    lateinit var fInput: ObjectInputStream


    suspend fun connectAndStartClient(context: Context): DataOrException<Client, Boolean, ResponseCode> {

        return withContext(Dispatchers.IO) {
            try {
                client = Client(Socket(Utils.getServerIP(context), PORT))
                val fSocket = Socket(Utils.getServerIP(context), FT_PORT)
                setStreams(context, fSocket)
                Log.d(TAG, "connectAndStartClient: ${client.socket}")
                DataOrException(client, false, ResponseCode.OK)

            } catch (e: Exception) {
                Log.e(TAG, "connect: ", e)
                DataOrException(null, false, ResponseCode.SOCKET_CONNECT_ERROR)
            }
        }

    }

    private fun setStreams(context: Context, fSocket: Socket) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                input = ObjectInputStream(client.socket.getInputStream())
                output = ObjectOutputStream(client.socket.getOutputStream())

                fInput = ObjectInputStream(fSocket.getInputStream())
                fOutput = ObjectOutputStream(fSocket.getOutputStream())
            }
            ClientReceiver.startReceiver(context)
        }
    }


}
