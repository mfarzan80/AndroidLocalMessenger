package net.group.androidlocalmessanger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.group.androidlocalmessanger.network.client.controller.AuthController
import net.group.androidlocalmessanger.ui.auth.AuthScreen
import net.group.androidlocalmessanger.ui.auth.AuthViewModel
import net.group.androidlocalmessanger.ui.connect.ConnectScreen
import net.group.androidlocalmessanger.ui.connect.ConnectViewModule
import net.group.androidlocalmessanger.ui.main.MainScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val connectViewModule = ConnectViewModule()
    val authViewModel = AuthController.authViewModel

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
            MainScreen(navController)
        }


    }
}