package net.group.androidlocalmessanger.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.GroupIcon
import net.group.androidlocalmessanger.ui.component.HSpacer
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
    }) {
        val modifier = Modifier.padding(horizontal = 20.dp)
        LazyColumn {

            items(groups.value.data!!) { group ->
                Row(modifier = modifier.padding(vertical = 10.dp).fillMaxWidth()) {
                    GroupIcon()
                    HSpacer(10.dp)
                    Column {
                        Text(text = group.name, style = MaterialTheme.typography.button)

                        Text(
                            text = group.users.size.toString(),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {

    val mainViewModule = MainViewModule()
    AndroidLocalMessangerTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            mainViewModule.groups.value.data = listOf(
                Group(name = "group1", Group.GROUP_TYPE_GROUP),
                Group(name = "channel1", Group.GROUP_TYPE_CHAT),
                Group(name = "chat", Group.GROUP_TYPE_CHANNEL),
            )
            MainScreen(rememberNavController(), mainViewModule)
        }
    }
}