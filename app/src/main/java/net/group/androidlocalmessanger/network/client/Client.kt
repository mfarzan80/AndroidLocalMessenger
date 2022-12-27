package net.group.androidlocalmessanger.network.client

import android.content.Context
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.server.TcpClientHandler
import net.group.androidlocalmessanger.network.server.TcpServerService
import net.group.androidlocalmessanger.utils.Utils
import java.net.Socket


class Client(val socket: Socket) {
    var user: User? = null

}