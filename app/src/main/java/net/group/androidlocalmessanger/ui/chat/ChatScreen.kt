package net.group.androidlocalmessanger.ui.chat

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.Message
import net.group.androidlocalmessanger.network.client.controller.ClientReceiver
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.HSpacer
import net.group.androidlocalmessanger.ui.component.VSpacer
import net.group.androidlocalmessanger.ui.main.GroupProfile
import net.group.androidlocalmessanger.ui.main.MainViewModule.Companion.getMe
import net.group.androidlocalmessanger.ui.navigation.Screen
import net.group.androidlocalmessanger.utils.FileUtil
import net.group.androidlocalmessanger.utils.Utils
import java.io.File


@Composable
fun ChatScreen(navController: NavController, chatViewModel: ChatViewModel) {
    val context = LocalContext.current
    val group = chatViewModel.groupWithUsers
    val chat = group.group.type == Group.GROUP_TYPE_CHAT

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val file = FileUtil.from(context, uri)
            val message = Message("", getMe(), group.group.groupId)
            message.attachedFileName = file.path
            chatViewModel.sendMessage(context, message)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            filePicker.launch("*/*")
        }
    }
    val text = remember {
        mutableStateOf("")
    }

    Log.d("Screen", "ChatScreen: " + chatViewModel.messages)
    ActivityView(
        topAppBar = {
            Row(
                modifier = Modifier.fillMaxSize()
                    .clickable {
                        if (chat)
                            navController.navigate(
                                route = Screen.UserDetailScreen.name + "/${
                                    group.getContact(
                                        getMe()
                                    ).userEmail
                                }"
                            )
                        else {
                            navController.navigate(
                                route = Screen.GroupDetailScreen.name + "/${
                                    group.group.groupId
                                }"
                            )
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                GroupProfile(group)
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = group.getGroupName(getMe()),
                    style = MaterialTheme.typography.h6
                )
            }
        }) {
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
                if (canSendMessage(group.group))
                    TextField(
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
                        value = text.value,
                        onValueChange = {
                            text.value = it
                        },
                        placeholder = { Text("Message", style = MaterialTheme.typography.caption) }
                    )
                else
                    Text(
                        text = "You can't send Message to this group",
                        modifier = Modifier.fillMaxWidth()
                            .background(color = MaterialTheme.colors.surface),
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center
                    )

                IconButton(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    onClick = {
                        if (text.value.isNotEmpty()) {
                            chatViewModel.sendMessage(
                                context,
                                Message(
                                    text = text.value,
                                    getMe(),
                                    group.group.groupId
                                )
                            )
                            text.value = ""
                        } else {
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) -> {
                                    filePicker.launch("*/*")
                                }
                                else -> {
                                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    }) {
                    Icon(
                        imageVector =
                        if (text.value.isNotEmpty())
                            Icons.Default.Send
                        else
                            Icons.Default.AttachFile,
                        contentDescription = "send"
                    )
                }
            }

        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MessageView(message: Message, ownerSender: Boolean, chat: Boolean) {
    val context = LocalContext.current
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
        nameColor = MaterialTheme.colors.primary
    }
    val localFile = remember { mutableStateOf<File?>(null) }
    val fileLoading = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        CoroutineScope(Dispatchers.Main).launch {
            if (message.attachedFileName != null) {
                fileLoading.value = true
                localFile.value = withContext(Dispatchers.IO) {
                    ClientReceiver.receiveFile(
                        context,
                        message.attachedFileName!!
                    )
                }
                fileLoading.value = false
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        Card(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .widthIn(max = 300.dp),
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
                if (message.attachedFileName != null)
                    Row {
                        if (fileLoading.value) {
                            CircularProgressIndicator(color = nameColor)
                        } else if (localFile.value != null) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = ""
                            )
                        }
                        HSpacer(10.dp)
                        Text(message.attachedFileName.toString().split("/").last())
                        Log.d("Screen", "MessageView: ${message.text}")
                    }
                else
                    Text(text = message.text)
            }
        }
    }
}

fun canSendMessage(group: Group): Boolean {
    val email = getMe().userEmail
    return if (group.adminIds.contains(email))
        true
    else if (group.type == Group.GROUP_TYPE_CHANNEL)
        false
    else !group.blackList.contains(email)

}


