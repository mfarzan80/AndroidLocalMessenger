package net.group.androidlocalmessanger.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.client.controller.ClientReceiver
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.HSpacer
import net.group.androidlocalmessanger.ui.component.OutlinedInput
import net.group.androidlocalmessanger.ui.main.MainViewModule
import net.group.androidlocalmessanger.ui.main.MainViewModule.Companion.getMe
import java.io.File

@Composable
fun AddConversationScreen(navController: NavController, mainViewModule: MainViewModule) {
    val users = mainViewModule.users.value.data!!
    val groupType = remember { mutableStateOf(Group.GROUP_TYPE_CHAT) }
    if (groupType.value != Group.GROUP_TYPE_CHAT && mainViewModule.users.value.data != null)
        GroupWizard(groupType, users, onConfirm = {
            mainViewModule.addGroup(it)
            navController.popBackStack()
        })


    Log.d("Screen", "AddConversationScreen: ")
    ActivityView(tittle = "Add Conversation") {
        if (mainViewModule.users.value.loading == true)
            CircularProgressIndicator()
        else {
            val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
            Column {
                Row(modifier = rowModifier) {
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                        groupType.value = Group.GROUP_TYPE_GROUP
                    }) {
                        Text("New Group")
                    }
                }
                Row(modifier = rowModifier) {
                    TextButton(modifier = Modifier.fillMaxWidth(), onClick = {
                        groupType.value = Group.GROUP_TYPE_CHANNEL
                    }) {
                        Text("New Channel")
                    }
                }
                UsersLazyColomn(
                    rowModifier = rowModifier,
                    users = users,
                )
                {
                    navController.popBackStack()
                    mainViewModule.getPvGroup(it)
                }
            }
        }
    }

}

@Composable
fun GroupWizard(
    groupType: MutableState<String>,
    users: List<User>,
    onConfirm: (group: GroupWithUsers) -> Unit
) {
    val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
    val selectedUsers = remember {
        mutableStateListOf<User>()
    }
    val name = remember { mutableStateOf("") }
    Dialog(onDismissRequest = {
        groupType.value = Group.GROUP_TYPE_CHAT
    }) {
        ActivityView(tittle = if (groupType.value == Group.GROUP_TYPE_GROUP) "New Group" else "New Channel") {

            Column {
                OutlinedInput(modifier = rowModifier, valueState = name, label = "Name")
                LazyColumn {
                    items(users) { user ->
                        if (user != getMe()) {
                            Row(
                                modifier = rowModifier.background(
                                    color = if (!selectedUsers.contains(user))
                                        MaterialTheme.colors.background
                                    else
                                        MaterialTheme.colors.primary
                                )
                                    .clickable {
                                        if (!selectedUsers.contains(user))
                                            selectedUsers.add(user)
                                        else
                                            selectedUsers.remove(user)
                                    }, verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserProfile(user)
                                HSpacer(10.dp)
                                Text(text = user.name, style = MaterialTheme.typography.button)
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 10.dp),
                    onClick = {
                        val group = Group(name = name.value, type = groupType.value)
                        group.adminIds.add(getMe().userEmail)
                        val groupUsers = ArrayList(selectedUsers)
                        groupUsers.add(getMe())
                        val groupWithUsers = GroupWithUsers(
                            group = group,
                            users = groupUsers
                        )
                        onConfirm(groupWithUsers)
                    }) {
                    Text("Create")
                }
            }
        }
    }
}


@Composable
fun UsersLazyColomn(
    modifier: Modifier = Modifier,
    rowModifier: Modifier = Modifier,
    users: List<User>,
    rowBackground: @Composable (user: User) -> Color = { MaterialTheme.colors.surface },
    rowClick: (user: User) -> Unit,

    ) {
    LazyColumn(modifier = modifier) {
        items(users) { user ->
            if (user != getMe()) {
                Row(
                    modifier = rowModifier.background(color = rowBackground(user))
                        .clickable {
                            rowClick(user)
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    UserProfile(user)
                    HSpacer(10.dp)
                    Text(text = user.name, style = MaterialTheme.typography.button)
                }
            }

        }
    }
}

@Composable
fun UserProfile(user: User, size: Dp = 50.dp) {
    val context = LocalContext.current
    val imageModifier = Modifier.size(size)
    var profileImg by remember { mutableStateOf<File?>(null) }
    Log.d("Screen", "ProfileImage:ServerPath: ${user.profilePath}")

    LaunchedEffect(key1 = true) {

        CoroutineScope(Dispatchers.Main).launch {

            if (user.showProfile && user.profilePath != null) {
                profileImg = withContext(Dispatchers.IO) {
                    ClientReceiver.receiveFile(context, user.profilePath!!, ".jpg")
                }
                Log.d("Screen", "ProfileImage:LocalPath: ${profileImg?.path}")


            }
        }

    }

    Card(shape = CircleShape, elevation = 0.dp) {

        if (!user.showProfile || user.profilePath == null) {
            Image(
                modifier = imageModifier,
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "ProfilePhoto"
            )
        } else if (profileImg == null) {
            CircularProgressIndicator()
        } else {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(profileImg!!.path)
                        .build()
                ),
                contentDescription = "ProfilePhoto",
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
        }

    }
}
