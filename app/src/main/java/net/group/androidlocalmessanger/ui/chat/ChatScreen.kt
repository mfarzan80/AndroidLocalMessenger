package net.group.androidlocalmessanger.ui.chat

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.Message
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.HSpacer
import net.group.androidlocalmessanger.ui.component.VSpacer
import net.group.androidlocalmessanger.ui.main.GroupProfile

@Composable
fun ChatScreen(navController: NavController, chatViewModel: ChatViewModel) {
    val text = remember {
        mutableStateOf("")
    }

    Log.d("Screen", "ChatScreen: " + chatViewModel.messages)
    ActivityView(
        topAppBar = {
            GroupProfile(chatViewModel.groupWithUsers)
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = chatViewModel.groupWithUsers.getGroupName(chatViewModel.getUser()),
                style = MaterialTheme.typography.h6
            )
        }) {
        val chat = chatViewModel.groupWithUsers.group.type == Group.GROUP_TYPE_CHAT
        Column(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
                items(chatViewModel.messages) {
                    MessageView(it, ownerSender = chatViewModel.isOwnerUser(it), chat)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(color = MaterialTheme.colors.surface)
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
                    value = text.value,
                    onValueChange = {
                        text.value = it
                    },
                    placeholder = { Text("Message", style = MaterialTheme.typography.caption) }
                )

                IconButton(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    onClick = {
                        chatViewModel.sendMessage(
                            Message(
                                text = text.value,
                                chatViewModel.getUser(),
                                chatViewModel.groupWithUsers.group.groupId
                            )
                        )
                        text.value = ""
                    }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "send")
                }
            }

        }
    }
}


@Composable
fun MessageView(message: Message, ownerSender: Boolean, chat: Boolean) {
    val arrangement: Arrangement.Horizontal
    val backColor: Color
    val nameColor: Color
    if (ownerSender) {
        arrangement = Arrangement.End
        backColor = MaterialTheme.colors.primary
        nameColor = MaterialTheme.colors.onPrimary
    } else {
        arrangement = Arrangement.Start
        backColor = MaterialTheme.colors.surface
        nameColor = MaterialTheme.colors.onBackground
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Card(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
            shape = RoundedCornerShape(5.dp),
            backgroundColor = backColor
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (!chat) {
                    Text(
                        text = message.sender.name,
                        style = MaterialTheme.typography.button,
                        color = nameColor
                    )
                    VSpacer(10.dp)
                }
                Text(text = message.text)
                Log.d("Screen", "MessageView: ${message.text}")
            }
        }
    }
}

