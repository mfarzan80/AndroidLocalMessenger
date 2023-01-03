package net.group.androidlocalmessanger.ui.chat

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.module.Message
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.client.controller.ClientController
import net.group.androidlocalmessanger.network.client.controller.MainController
import net.group.androidlocalmessanger.ui.main.MainViewModule.Companion.getMe

class ChatViewModel(val groupWithUsers: GroupWithUsers) : ViewModel() {
    val messages = mutableStateListOf<Message>()
    val users = mutableStateListOf<User>()

    init {
        messages.addAll(groupWithUsers.group.messages)
        users.addAll(groupWithUsers.users)
        Log.d("ClientController", "ChatViewModel init: $messages")
    }

    fun sendMessage(context: Context, message: Message) {
        viewModelScope.launch {
            MainController.sendMessage(context, message)

            messages.add(message)
            Log.d("ClientController", "sendMessage: END")
        }
    }

    fun isOwnerUser(message: Message): Boolean {
        return getMe() == message.sender
    }

    fun updateGroupUsers(users: List<User>) {
        this.users.clear()
        this.users.addAll(users)
    }


}