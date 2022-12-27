package net.group.androidlocalmessanger.ui.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.Message

class ChatViewModel(val group: Group) : ViewModel() {
    val messages: SnapshotStateList<Message> = SnapshotStateList()

    init {
        messages.addAll(group.messages)
    }

}