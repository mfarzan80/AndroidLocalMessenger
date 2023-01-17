package net.group.androidlocalmessanger.ui.connect

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.talhafaki.composablesweettoast.util.SweetToastUtil
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.LoadingErrorDataView
import net.group.androidlocalmessanger.ui.component.VSpacer
import net.group.androidlocalmessanger.ui.navigation.Screen

@Composable
fun ConnectScreen(navController: NavController, connectViewModule: ConnectViewModule) {
    val context = LocalContext.current

    ActivityView(modifier = Modifier.fillMaxSize()) {
        Log.d("Screen", "ConnectScreen: ")
        Column(
            modifier = Modifier.fillMaxSize().padding(30.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val client = connectViewModule.client.value

            if (!connectViewModule.serverRunning.value) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Welcome",
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Center
                )
                VSpacer(20.dp)
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    connectViewModule.startServer(context)

                }) {
                    Text("Start server")
                }

                VSpacer()

                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = {
                    connectViewModule.connectToServer(context)
                }) {
                    Text("Connect To server")
                }

                LoadingErrorDataView(dataOrException = client) {
                    SweetToastUtil.SweetSuccess("Connected to server")
                    LaunchedEffect(key1 = true) {
                        navController.popBackStack()
                        navController.navigate(Screen.AuthScreen.name)
                    }

                }
            } else {
                Text("Server running...")
            }

        }

    }

}

