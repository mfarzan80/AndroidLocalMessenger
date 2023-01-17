package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Order
import net.group.androidlocalmessanger.module.OrderData
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.module.ResponseType
import net.group.androidlocalmessanger.network.client.controller.ClientController.TAG
import net.group.androidlocalmessanger.network.client.controller.ClientController.fInput
import net.group.androidlocalmessanger.network.client.controller.ClientController.input
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshGroups
import net.group.androidlocalmessanger.network.client.controller.MainController.refreshUsers
import net.group.androidlocalmessanger.network.client.controller.MainController.updateGroup
import net.group.androidlocalmessanger.network.client.controller.UserController.authResponse
import net.group.androidlocalmessanger.network.client.controller.UserController.updateUserResponse
import net.group.androidlocalmessanger.network.server.ClientHandler
import net.group.androidlocalmessanger.utils.Catcher
import net.group.androidlocalmessanger.utils.Utils
import java.io.File
import java.io.IOException
import java.util.UUID

object ClientReceiver {

    var responseReceiver = true

    var receiving = false

    suspend fun startReceiver(context: Context) {

        responseReceiver = true
        withContext(Dispatchers.IO) {

            while (responseReceiver) {
                try {

                    val response = input.readObject() as Response<*>
                    Log.d(TAG, "Receiver: $response")
                    when (response.responseType) {
                        ResponseType.Login -> authResponse(response, true)
                        ResponseType.Register -> authResponse(response, false)
                        ResponseType.UpdatedUser -> updateUserResponse(response)
                        ResponseType.AllGroups -> refreshGroups(response)
                        ResponseType.UpdatedGroup -> updateGroup(response)
                        ResponseType.AllUsers -> refreshUsers(response)
                        ResponseType.SendingFile -> {
                            //    stopResponseReceiver()
                        }
                        null -> {}
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "startReceiver: ", e)
                    break
                }

            }

        }

    }


    suspend fun receiveFile(
        context: Context,
        fileName: String,
        suffix: String = "." + fileName.split(".").last()
    ): File {
        val catcher = Catcher(context)
        val localPath = catcher.getLocalPathByFileName(fileName)
        Log.d(TAG, "receiveFile:Server: $fileName")
        Log.d(TAG, "receiveFile:Local: $localPath")
        return if (localPath == null) {
            return withContext(Dispatchers.IO) {
                while (receiving){}
                receiving = true
                Sender.sendRequest(OrderData(Order.GetFile, fileName))
                val downloadedFile =
                    File(
                        Utils.getCashFolder(context).absolutePath + File.separator + UUID.randomUUID()
                            .toString() + suffix
                    )
                downloadedFile.createNewFile()
                Log.d(ClientHandler.TAG, "receiveFile start: " + downloadedFile.path)
                Utils.receiveFile(downloadedFile, fInput)
                Log.d(ClientHandler.TAG, "receiveFile Finish: " + downloadedFile.path)
                Utils.saveToDownload(context, downloadedFile)
                catcher.saveLocalPath(fileName, downloadedFile.path)
                receiving = false
                downloadedFile
            }
        } else {
            File(localPath)
        }
    }


}

