package net.group.androidlocalmessanger.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.GroupIcon
import net.group.androidlocalmessanger.ui.component.HSpacer
import net.group.androidlocalmessanger.ui.component.OutlinedInput
import net.group.androidlocalmessanger.ui.main.MainViewModule
import net.group.androidlocalmessanger.ui.navigation.Screen
import net.group.androidlocalmessanger.utils.ListTypeConverters.Companion.gson

@Composable
fun AddConversationScreen(navController: NavController, mainViewModule: MainViewModule) {

    val groupType = remember { mutableStateOf(Group.GROUP_TYPE_CHAT) }
    if (groupType.value != Group.GROUP_TYPE_CHAT && mainViewModule.users.value.data != null)
        UsersDialog(groupType, mainViewModule.users.value.data!!, onConfirm = {
            mainViewModule.addGroup(it)
            navController.popBackStack()
        })

    LaunchedEffect(key1 = true) {
        mainViewModule.sendUsersRequest()
    }

    Log.d("Screen", "AddConversationScreen: ")
    ActivityView(tittle = "Add Conversation") {
        if (mainViewModule.users.value.loading == true)
            CircularProgressIndicator()
        else {
            val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
            Column {
                Row(modifier = rowModifier) {
                    TextButton(onClick = {
                        groupType.value = Group.GROUP_TYPE_GROUP
                    }) {
                        Text("New Group")
                    }
                }
                Row(modifier = rowModifier) {
                    TextButton(onClick = {
                        groupType.value = Group.GROUP_TYPE_CHANNEL
                    }) {
                        Text("New Channel")
                    }
                }
                UsersLazyColomn(
                    rowModifier = rowModifier,
                    users = mainViewModule.users.value.data!!,
                )
                {
                    navController.popBackStack()
                    navController.navigate(
                        Screen.ChatScreen.name + "/${
                            gson.toJson(mainViewModule.getPvGroup(it))
                        }"
                    )
                }
            }
        }
    }

}

@Composable
fun UsersDialog(
    groupType: MutableState<String>,
    users: List<User>,
    onConfirm: (group: GroupWithUsers) -> Unit
) {
    val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
    val selectedUsers = remember {
        mutableSetOf<User>()
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
                                }) {
                            GroupIcon()
                            HSpacer(10.dp)
                            Text(text = user.name, style = MaterialTheme.typography.button)
                        }

                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 10.dp),
                    onClick = {
                        val group = GroupWithUsers(
                            group = Group(name = name.value, type = groupType.value),
                            users = selectedUsers.toList()
                        )
                        onConfirm(group)
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
            Row(
                modifier = rowModifier.background(color = rowBackground(user))
                    .clickable {
                        rowClick(user)
                    }) {
                GroupIcon()
                HSpacer(10.dp)
                Text(text = user.name, style = MaterialTheme.typography.button)
            }

        }
    }
}