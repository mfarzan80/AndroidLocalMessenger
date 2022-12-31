package net.group.androidlocalmessanger.network.client.controller

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.module.ResponseType
import net.group.androidlocalmessanger.network.client.controller.AuthController.authResponse

import net.group.androidlocalmessanger.network.client.controller.ClientController.TAG
import net.group.androidlocalmessanger.network.client.controller.ClientController.input
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshGroups
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshUsers
import net.group.androidlocalmessanger.network.client.controller.MainController.updateGroup
import java.io.IOException

object ClientReceiver {


    suspend fun startReceiver() {


        withContext(Dispatchers.IO) {

            while (true) {
                try {

                    val response = input.readObject() as Response<*>
                    Log.d(TAG, "Receiver: $response")
                    when (response.responseType) {
                        ResponseType.Login -> authResponse(response, true)
                        ResponseType.Register -> authResponse(response, false)
                        ResponseType.AllGroups -> refreshGroups(response)
                        ResponseType.UpdateGroup -> updateGroup(response)
                        ResponseType.AllUsers -> refreshUsers(response)
                        null -> {}
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "startReceiver: ", e)
                    break
                }

            }

        }

    }


}


