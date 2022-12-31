package net.group.androidlocalmessanger.network.server

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.R
import net.group.androidlocalmessanger.di.AppModule
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.client.Client
import net.group.androidlocalmessanger.repository.GroupRepository
import net.group.androidlocalmessanger.repository.UserRepository
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean


class ServerService : Service() {

    companion object {
        const val TAG = "TcpServerService"
        const val PORT = 9876
        const val HOST = "192.168.43.1"
        val working = AtomicBoolean(false)
        val userToClient = HashMap<User, ClientHandler>()
    }

    lateinit var userRepository: UserRepository
    lateinit var groupRepository: GroupRepository


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        working.set(true)
        startMeForeground()
        val dao = AppModule.getDatabaseDao(applicationContext)
        userRepository = UserRepository(dao)
        groupRepository = GroupRepository(dao)
        CoroutineScope(Dispatchers.IO).launch {
            startServer()
        }
    }

    private suspend fun startServer() {

        var socket: Socket? = null
        withContext(Dispatchers.IO) {
            try {

                val server = ServerSocket(PORT)

                while (working.get()) {

                    socket = server.accept()

                    val clientHandler =
                        ClientHandler(userRepository, groupRepository, Client(socket!!))
                    clientHandler.handle()

                    Log.i(TAG, "New client: $socket")

                }
            } catch (e: IOException) {
                Log.e(TAG, "TcpServerService: ", e)
                try {
                    socket?.close()
                } catch (ex: IOException) {
                    Log.e(TAG, "socketCloseException: ", ex)
                }
            }
        }

    }


    override fun onDestroy() {
        working.set(false)
    }

    private fun startMeForeground() {
        val tittle = "Android Local messenger running"
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = packageName
            val channelName = "Tcp Server Background Service"
            val chan =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
            notification = notificationBuilder.setOngoing(true)
                .setContentTitle(tittle)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        } else {
            val notificationBuilder = NotificationCompat.Builder(this)
            notification = notificationBuilder.setOngoing(true)
                .setContentTitle(tittle)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        }
        startForeground(2, notification)
    }


}