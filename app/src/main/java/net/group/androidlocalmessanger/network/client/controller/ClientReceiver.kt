package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.module.ResponseType
import net.group.androidlocalmessanger.network.client.controller.ClientController.TAG
import net.group.androidlocalmessanger.network.client.controller.ClientController.input
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshGroups
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshUsers
import net.group.androidlocalmessanger.network.client.controller.MainController.updateGroup
import net.group.androidlocalmessanger.network.client.controller.UserController.authResponse
import net.group.androidlocalmessanger.network.client.controller.UserController.updateUserResponse
import net.group.androidlocalmessanger.network.server.ClientHandler
import net.group.androidlocalmessanger.utils.Utils
import java.io.File
import java.io.IOException

object ClientReceiver {

    suspend fun startReceiver(context: Context) {


        withContext(Dispatchers.IO) {

            while (true) {
                try {

                    val response = input.readObject() as Response<*>
                    Log.d(TAG, "Receiver: $response")
                    when (response.responseType) {
                        ResponseType.Login -> authResponse(response, true)
                        ResponseType.Register -> authResponse(response, false)
                        ResponseType.UpdatedUser -> updateUserResponse(response)
                        ResponseType.AllGroups -> refreshGroups(response)
                        ResponseType.UpdatedGroup -> updateGroup(response)
                        ResponseType.AllUsers -> refreshUsers(context, response)


                        null -> {}
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "startReceiver: ", e)
                    break
                }

            }

        }

    }


    fun receiveFile(context: Context, fileName: String): File {
        val receivedFile =
            File(Utils.getCashFolder(context).absolutePath + File.separator + fileName)
        receivedFile.createNewFile()
        Log.d(ClientHandler.TAG, "receiveFile start: " + receivedFile.path)
        Utils.receiveFile(receivedFile, input)
        Log.d(ClientHandler.TAG, "receiveFile Finish: " + receivedFile.path)
        Utils.saveToDownload(context, receivedFile)
        return receivedFile
    }

}


