package net.group.androidlocalmessanger.network.client.controller

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.OrderData
import net.group.androidlocalmessanger.network.client.controller.ClientController.TAG
import net.group.androidlocalmessanger.network.client.controller.ClientController.output
import net.group.androidlocalmessanger.utils.Utils
import java.io.File

object Sender {

    suspend fun sendRequest(orderData: OrderData<*>) {

        withContext(Dispatchers.IO) {
            output.writeObject(orderData)
            output.flush()
            Log.d(TAG, "sendRequest: $orderData")
        }

    }

    suspend fun uploadFile(path: String) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "uploadFile start: $path")
            Utils.sendFile(
                File(path),
                output
            )
            Log.d(TAG, "uploadFile end: $path")
        }
    }
}