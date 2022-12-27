package net.group.androidlocalmessanger.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.GroupIcon
import net.group.androidlocalmessanger.ui.component.HSpacer
import net.group.androidlocalmessanger.ui.main.MainViewModule
import net.group.androidlocalmessanger.ui.navigation.Screen

@Composable
fun AddConversationScreen(navController: NavController, mainViewModule: MainViewModule) {
    mainViewModule.sendUsersRequest()
    val gson = Gson()
    ActivityView(tittle = "Add Conversation") {
        if (mainViewModule.users.value.loading == true)
            CircularProgressIndicator()

        LazyColumn {
            items(mainViewModule.users.value.data!!) { user ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
                        .clickable {
                            navController.navigate(
                                Screen.ChatScreen.name + "/${
                                    gson.toJson(mainViewModule.getPvGroup(user))
                                }"
                            )
                        }) {
                    GroupIcon()
                    HSpacer(10.dp)
                    Text(text = user.name, style = MaterialTheme.typography.button)
                }

            }
        }
    }
}