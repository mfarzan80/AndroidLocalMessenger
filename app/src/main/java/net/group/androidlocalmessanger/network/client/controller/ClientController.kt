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
import net.group.androidlocalmessanger.network.server.TcpServerService.Companion.PORT
import net.group.androidlocalmessanger.utils.Utils
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

object ClientController {

    const val TAG = "ClientController"

    lateinit var client: Client
    lateinit var input: ObjectInputStream
    lateinit var output: ObjectOutputStream


    suspend fun connectAndStartClient(context: Context): DataOrException<Client, Boolean, ResponseCode> {

        return withContext(Dispatchers.IO) {
            try {
                val socket =
                    Socket(Utils.getServerIP(context), PORT)
                client = Client(socket)
                setStreams()
                Log.d(TAG, "connectAndStartClient: $socket")
                DataOrException(client, false, ResponseCode.OK)

            } catch (e: Exception) {
                Log.e(TAG, "connect: ", e)
                DataOrException(null, false, ResponseCode.SOCKET_CONNECT_ERROR)
            }
        }

    }

    private fun setStreams() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                input = ObjectInputStream(client.socket.getInputStream())
                output = ObjectOutputStream(client.socket.getOutputStream())
            }
            ClientReceiver.startReceiver()
        }
    }


}
