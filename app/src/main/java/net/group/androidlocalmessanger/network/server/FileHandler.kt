package net.group.androidlocalmessanger.network.server

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.OrderData
import net.group.androidlocalmessanger.utils.Catcher
import net.group.androidlocalmessanger.utils.Utils
import java.io.File
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class FileHandler(private val socket: Socket) {
    private lateinit var fOutput: ObjectOutputStream
    private lateinit var fInput: ObjectInputStream
    private var receiving = false

    suspend fun setStreams() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    fOutput = ObjectOutputStream(socket.getOutputStream())
                    fInput = ObjectInputStream(socket.getInputStream())

                } catch (e: IOException) {
                    Log.e(ClientHandler.TAG, "handle: ", e)
                    try {
                        fInput.close()
                        fOutput.close()
                    } catch (ex: IOException) {
                        Log.e(ClientHandler.TAG, "handle: ", e)
                    }

                }
            }
        }
    }

    suspend fun receiveFile(context: Context, fileName: String): File {
        return withContext(Dispatchers.IO) {

            val catcher = Catcher(context)
            val receivedFile =
                File(Utils.getCashFolder(context).absolutePath + File.separator + System.nanoTime() + fileName)
            Log.d(ClientHandler.TAG, "receiveFile file name: " + receivedFile.path)
            receivedFile.createNewFile()
            Log.d(ClientHandler.TAG, "receiveFile start: " + receivedFile.path)
            Utils.receiveFile(receivedFile, fInput)
            catcher.saveLocalPath(fileName, receivedFile.path)
            Log.d(ClientHandler.TAG, "receiveFile Finish: " + receivedFile.path)
            Utils.saveToDownload(context, receivedFile)
            receiving = false
            receivedFile
        }
    }

    suspend fun sendFile(context: Context, orderData: OrderData<*>) {
        val catcher = Catcher(context)
        val filePath = orderData.data as String
        withContext(Dispatchers.IO) {
            Utils.sendFile(File(catcher.getLocalPathByFileName(filePath)), fOutput)
        }
    }
}