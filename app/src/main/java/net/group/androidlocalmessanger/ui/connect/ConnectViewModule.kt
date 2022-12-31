package net.group.androidlocalmessanger.ui.connect

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.ResponseCode
import net.group.androidlocalmessanger.network.client.Client
import net.group.androidlocalmessanger.network.client.controller.ClientController
import net.group.androidlocalmessanger.network.server.ServerService

class ConnectViewModule : ViewModel() {

    val client: MutableState<DataOrException<Client, Boolean, ResponseCode>> = mutableStateOf(
        DataOrException(
            null, false, null
        )
    )
    val serverRunning = mutableStateOf(ServerService.working.get())

    fun connectToServer(context: Context) {
        viewModelScope.launch {
            startLoading()
            val response: DataOrException<Client, Boolean, ResponseCode>

            response = ClientController.connectAndStartClient(context)

            client.value = response
            Log.d("ClientController", "connectToServer: ${response.loading}")

        }

    }

    fun startServer(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, ServerService::class.java))
        } else {
            context.startService(Intent(context, ServerService::class.java))
        }
        serverRunning.value = true
    }

    fun startLoading() {
        client.value = client.value.copy(loading = true)
    }

    fun stopLoading() {
        client.value = client.value.copy(loading = false)
    }

}