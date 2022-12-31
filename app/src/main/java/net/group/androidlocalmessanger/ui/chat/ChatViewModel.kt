package net.group.androidlocalmessanger.ui.chat

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.module.Message
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.client.controller.ClientController
import net.group.androidlocalmessanger.network.client.controller.MainController

class ChatViewModel(val groupWithUsers: GroupWithUsers) : ViewModel() {
    val messages = mutableStateListOf<Message>()

    init {
        messages.addAll(groupWithUsers.group.messages)
        Log.d("ClientController", "ChatViewModel init: $messages")
    }

    fun sendMessage(message: Message) {
        messages.add(message)
        viewModelScope.launch {

            MainController.sendMessage(message)
        }
    }

    fun getUser(): User {
        return ClientController.client.user!!
    }

    fun isOwnerUser(message: Message): Boolean {
        return getUser() == message.sender
    }


}