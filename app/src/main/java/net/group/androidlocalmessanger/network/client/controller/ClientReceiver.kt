package net.group.androidlocalmessanger.network.client.controller

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.module.ResponseTypes
import net.group.androidlocalmessanger.network.client.controller.AuthController.loginResponse
import net.group.androidlocalmessanger.network.client.controller.AuthController.registerResponse
import net.group.androidlocalmessanger.network.client.controller.ClientController.TAG
import net.group.androidlocalmessanger.network.client.controller.ClientController.input
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshGroups
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshUsers
import java.io.IOException

object ClientReceiver {


    suspend fun startReceiver() {


        withContext(Dispatchers.IO) {

            while (true) {
                try {

                    val response = input.readObject() as Response<*>
                    Log.d(TAG, "Receiver: $response")
                    when (response.responseTypes) {
                        ResponseTypes.Login -> loginResponse(response)
                        ResponseTypes.Register -> registerResponse(response)
                        ResponseTypes.AllGroups -> refreshGroups(response)
                        ResponseTypes.AllUsers -> refreshUsers(response)
                        null -> TODO()
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "startReceiver: ", e)
                    break
                }

            }

        }

    }

}


