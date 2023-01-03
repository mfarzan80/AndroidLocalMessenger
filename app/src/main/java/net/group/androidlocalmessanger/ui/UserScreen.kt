package net.group.androidlocalmessanger.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.HSpacer
import net.group.androidlocalmessanger.ui.component.VSpacer

/*

    lateinit var name: String
    var profilePath: String? = null
    var phoneNumber: String? = null
    var userName: String? = null

    var showPhoneNumber: Boolean = false
    var showProfile: Boolean = true
 */
@Composable
fun UserScreen(navController: NavController, user: User) {
    ActivityView(topAppBar = {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            UserProfile(user, 120.dp)
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = user.name,
                style = MaterialTheme.typography.h5
            )
        }
    }) {
        Card(modifier = Modifier.fillMaxWidth().padding(10.dp), shape = RoundedCornerShape(10.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Information", color = MaterialTheme.colors.primary)
                VSpacer(20.dp)
                InfoColumn(user.userEmail, "Email")
                if (user.userName != null)
                    InfoColumn(user.userName!!, "Username")
                if (user.phoneNumber != null && user.showPhoneNumber)
                    InfoColumn(user.phoneNumber!!, "Phone number")
            }
        }
    }

}

@Composable
fun InfoColumn(value: String, label: String) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Text(text = value, style = MaterialTheme.typography.h6)
        HSpacer(space = 5.dp)
        Text(text = label, style = MaterialTheme.typography.caption)
    }

}