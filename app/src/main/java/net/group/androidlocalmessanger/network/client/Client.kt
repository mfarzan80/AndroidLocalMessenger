package net.group.androidlocalmessanger.network.client

import net.group.androidlocalmessanger.module.User
import java.net.Socket

class Client(val socket: Socket) {
    var user: User? = null
}