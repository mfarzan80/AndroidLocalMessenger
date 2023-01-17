package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Order
import net.group.androidlocalmessanger.module.OrderData
import net.group.androidlocalmessanger.network.client.controller.ClientController.TAG
import net.group.androidlocalmessanger.network.client.controller.ClientController.fOutput
import net.group.androidlocalmessanger.network.client.controller.ClientController.input
import net.group.androidlocalmessanger.network.client.controller.ClientController.output
import net.group.androidlocalmessanger.utils.Catcher
import net.group.androidlocalmessanger.utils.Utils
import net.group.androidlocalmessanger.utils.Utils.getFileName
import java.io.File

object Sender {

    private var sending = false

    suspend fun sendRequest(orderData: OrderData<*>) {

        withContext(Dispatchers.IO) {
            output.writeObject(orderData)
            output.flush()
            Log.d(TAG, "sendRequest: $orderData")
        }

    }


    suspend fun uploadFile(name: String, path: String, context: Context) {
        withContext(Dispatchers.IO) {
            while (sending) { }
            sending = true
            val catcher = Catcher(context)
            Log.d(TAG, "uploadFile start: $path")
            Utils.sendFile(
                File(path),
                fOutput
            )
            Log.d(TAG, "uploadFile end: $path")
            catcher.saveLocalPath(name, path)
            sending = false
        }
    }
}