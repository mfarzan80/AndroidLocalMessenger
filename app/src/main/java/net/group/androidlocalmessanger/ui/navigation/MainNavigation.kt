package net.group.androidlocalmessanger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.network.client.controller.AuthController
import net.group.androidlocalmessanger.network.client.controller.MainController
import net.group.androidlocalmessanger.network.client.controller.MainController.groupIdToChatViewModel
import net.group.androidlocalmessanger.ui.AddConversationScreen
import net.group.androidlocalmessanger.ui.chat.ChatScreen
import net.group.androidlocalmessanger.ui.auth.AuthScreen
import net.group.androidlocalmessanger.ui.chat.ChatViewModel
import net.group.androidlocalmessanger.ui.connect.ConnectScreen
import net.group.androidlocalmessanger.ui.connect.ConnectViewModule
import net.group.androidlocalmessanger.ui.main.MainScreen
import net.group.androidlocalmessanger.utils.ListTypeConverters.Companion.gson

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val connectViewModule = ConnectViewModule()
    val authViewModel = AuthController.authViewModel
    val mainViewModule = MainController.mainViewModule


    NavHost(
        navController = navController,
        startDestination = Screen.ConnectScreen.name
    ) {


        composable(route = Screen.ConnectScreen.name) {
            ConnectScreen(navController, connectViewModule)
        }

        composable(route = Screen.AuthScreen.name) {
            AuthScreen(navController, authViewModel)
        }

        composable(route = Screen.MainScreen.name) {
            MainScreen(navController, mainViewModule)
        }

        composable(route = Screen.AddConversationScreen.name) {
            AddConversationScreen(navController, mainViewModule)
        }

        composable(route = Screen.ChatScreen.name + "/{group}", arguments = listOf(
            navArgument(name = "group") {
                type = NavType.StringType
            }
        )) {
            val group = gson.fromJson(it.arguments!!["group"] as String, GroupWithUsers::class.java)
            var chatViewModel =
                groupIdToChatViewModel[group.group.groupId]
            if (chatViewModel == null) {
                chatViewModel = ChatViewModel(group)
                groupIdToChatViewModel[group.group.groupId] = chatViewModel
            }
            ChatScreen(navController, chatViewModel)
        }

        composable(route = Screen.SettingScreen.name) {

        }

    }
}