package net.group.androidlocalmessanger.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.ui.UserProfile
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.GroupIcon
import net.group.androidlocalmessanger.ui.component.HSpacer
import net.group.androidlocalmessanger.ui.component.VSpacer
import net.group.androidlocalmessanger.ui.main.MainViewModule.Companion.getMe
import net.group.androidlocalmessanger.ui.navigation.Screen
import net.group.androidlocalmessanger.ui.theme.AndroidLocalMessangerTheme

@Composable
fun MainScreen(navController: NavController, mainViewModule: MainViewModule) {

    val groups = mainViewModule.groups
    ActivityView(floatingActionButton = {
        FloatingActionButton(onClick = {
            navController.navigate(Screen.AddConversationScreen.name)
        }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "add",
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }, topAppBar = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f).padding(horizontal = 10.dp),
                text = "Android Local Messenger",
                style = MaterialTheme.typography.h6
            )

            IconButton(onClick = {
                navController.navigate(route = Screen.SettingScreen.name)
            }) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "setting")
            }

        }
    }) {
        val modifier = Modifier.padding(horizontal = 20.dp)
        if (mainViewModule.groupLoading.value) {
            Dialog(
                onDismissRequest = { },
                DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {

            LazyColumn {

                items(groups.values.toList()) { group ->
                    Row(
                        modifier = modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Screen.ChatScreen.name + "/${
                                        group.group.groupId
                                    }"
                                )
                            }, verticalAlignment = Alignment.CenterVertically
                    ) {

                        GroupProfile(group)
                        HSpacer(10.dp)
                        Column {
                            Text(
                                text = group.getGroupName(getMe()),
                                style = MaterialTheme.typography.h6
                            )
                            if (group.group.messages.isNotEmpty()) {
                                VSpacer(5.dp)
                                Text(
                                    text = group.group.messages.last().textOrFile(),
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }

                    }

                }

            }
        }
    }

}

@Composable
fun GroupProfile(group: GroupWithUsers, size: Dp = 50.dp) {
    if (group.group.type == Group.GROUP_TYPE_CHAT) {
        UserProfile(group.getContact(getMe()), size)
    } else {
        GroupIcon()
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {

    val mainViewModule = MainViewModule()
    AndroidLocalMessangerTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
//            mainViewModule.setGroups(
//                listOf(
//                    Group(name = "group1", Group.GROUP_TYPE_GROUP),
//                    Group(name = "channel1", Group.GROUP_TYPE_CHAT),
//                    Group(name = "chat", Group.GROUP_TYPE_CHANNEL),
//                )
//            )
//            MainScreen(rememberNavController(), mainViewModule)
        }
    }
}