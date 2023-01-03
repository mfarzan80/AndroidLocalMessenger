package net.group.androidlocalmessanger.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.network.client.controller.ClientController
import net.group.androidlocalmessanger.ui.chat.ChatViewModel
import net.group.androidlocalmessanger.ui.component.*
import net.group.androidlocalmessanger.ui.main.GroupProfile
import net.group.androidlocalmessanger.ui.main.MainViewModule
import net.group.androidlocalmessanger.ui.main.MainViewModule.Companion.getMe
import net.group.androidlocalmessanger.ui.navigation.Screen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupScreen(
    navController: NavController,
    mainViewModule: MainViewModule,
    chatViewModel: ChatViewModel
) {
    val addUsersDialogState = remember { mutableStateOf(false) }
    val group = chatViewModel.groupWithUsers
    val users = chatViewModel.users



    if (addUsersDialogState.value)
        AddUserDialog(mainViewModule.users.value.data!!, group) { newGroup ->
            mainViewModule.sendUpdatedGroup(newGroup)
            chatViewModel.updateGroupUsers(newGroup.users)
        }

    ActivityView(topAppBar = {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            GroupProfile(group, 80.dp)
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                Text(
                    text = group.group.name,
                    style = MaterialTheme.typography.h5
                )
                Text(
                    text = group.users.size.toString() + " members",
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }) {
        Card(modifier = Modifier.padding(10.dp), shape = RoundedCornerShape(10.dp)) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                InfoColumn(group.group.type, "Group type")
                val isMeAdmin = group.group.adminIds.contains(getMe().userEmail)
                if (group.group.type != Group.GROUP_TYPE_CHANNEL || isMeAdmin) {
                    VSpacer(20.dp)
                    Text(text = "Members", color = MaterialTheme.colors.primary)
                    VSpacer(13.dp)

                    LazyColumn {
                        items(users) { user ->
                            val isAdmin = group.group.adminIds.contains(user.userEmail)
                            val block = group.group.blackList.contains(user.userEmail)
                            var expanded by remember { mutableStateOf(false) }
                            Box {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 7.dp)
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate(
                                                    route = Screen.UserDetailScreen.name + "/${
                                                        user.userEmail
                                                    }"
                                                )
                                            },
                                            onLongClick = {
                                                if (isMeAdmin) {
                                                    expanded = true
                                                }
                                            },
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    UserProfile(user)
                                    HSpacer(10.dp)
                                    Text(user.name, modifier = Modifier.weight(1f))
                                    if (isAdmin) {
                                        HSpacer(5.dp)
                                        Text("Admin", style = MaterialTheme.typography.caption)
                                    } else if (block) {
                                        HSpacer(5.dp)
                                        Text("Block", style = MaterialTheme.typography.caption)
                                    }
                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(onClick = {
                                        group.group.adminIds.add(user.userEmail)
                                        mainViewModule.sendUpdatedGroup(group)
                                    }) {
                                        if (!isAdmin)
                                            Text("Promote to admin")
                                        else
                                            Text("Remove from admins")
                                    }
                                    DropdownMenuItem(onClick = {
                                        group.group.blackList.add(user.userEmail)
                                        mainViewModule.sendUpdatedGroup(group)
                                    }) {
                                        if (block)
                                            Text("Unblock")
                                        else
                                            Text("Block")
                                    }
                                    DropdownMenuItem(onClick = {
                                        val groupUsers = ArrayList(group.users)
                                        groupUsers.remove(user)
                                        group.users = groupUsers
                                        mainViewModule.sendUpdatedGroup(group)
                                        users.remove(user)
                                    }) {
                                        Text("Kick")
                                    }
                                }

                            }
                        }

                    }

                    VSpacer(20.dp)

                    Button(onClick = {
                        addUsersDialogState.value = true
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add member")
                    }

                }
            }
        }
    }
}

@Composable
fun AddUserDialog(
    users: List<User>,
    group: GroupWithUsers,
    onConfirm: (newGroup: GroupWithUsers) -> Unit
) {
    val rowModifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
    val selectedUsers = remember {
        mutableStateListOf<User>()
    }
    ActivityView(tittle = "Add Member") {

        Column {
            LazyColumn {
                items(users) { user ->
                    if (group.users.contains(user)) return@items
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
                    val newMembers = ArrayList(group.users)
                    newMembers.addAll(selectedUsers)
                    val newGroup = GroupWithUsers(group.group, newMembers)
                    onConfirm(newGroup)
                }) {
                Text("Add")
            }
        }
    }
}
