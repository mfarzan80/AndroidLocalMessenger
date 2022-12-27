package net.group.androidlocalmessanger.network.client.controller

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.OrderData
import net.group.androidlocalmessanger.network.client.controller.ClientController.TAG
import net.group.androidlocalmessanger.network.client.controller.ClientController.output

object Sender {

    suspend fun sendRequest(orderData: OrderData<*>) {

        withContext(Dispatchers.IO) {
            output.writeObject(orderData)
            output.flush()
            Log.d(TAG, "sendRequest: $orderData")
        }

    }
}