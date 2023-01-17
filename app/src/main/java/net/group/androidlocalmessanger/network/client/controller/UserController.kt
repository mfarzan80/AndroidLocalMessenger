package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.controller.Sender.sendRequest
import net.group.androidlocalmessanger.network.client.controller.Sender.uploadFile
import net.group.androidlocalmessanger.ui.auth.UserViewModel
import net.group.androidlocalmessanger.utils.Utils.getFileName
import java.io.File

object UserController {
    val userViewModel = UserViewModel()

    fun authResponse(response: Response<*>, login: Boolean) {
        if (response.code == ResponseCode.OK) {
            val userWithGroups = response.data as UserWithGroups
            ClientController.client.user = userWithGroups.user
        }
        if (login)
            userViewModel.loginResponse(response as Response<UserWithGroups>)
        else
            userViewModel.registerResponse(response as Response<UserWithGroups>)

    }

    fun updateUserResponse(response: Response<*>) {
        if (response.code == ResponseCode.OK) {
            val user = response.data as User
            ClientController.client.user = user
        }
        userViewModel.updateUserResponse(response as Response<User>)
    }


    suspend fun sendLoginRequest(user: User) {
        sendRequest(OrderData(Order.Login, user))
    }

    suspend fun sendRegisterRequest(user: User) {
        sendRequest(OrderData(Order.Register, user))
    }

    suspend fun sendUpdateUserRequest(context: Context, user: User) {
        if (user.profilePath != ClientController.client.user!!.profilePath) {
            if (user.profilePath != null) {
                val profilePath = user.profilePath.toString()
                val fileName = getFileName(profilePath)
                user.profilePath = fileName
                sendRequest(OrderData(Order.UpdateUser, user))
                sendRequest(OrderData(Order.UpdateProfile, user))
                uploadFile(fileName, profilePath, context)
                return
            }
        }
        sendRequest(OrderData(Order.UpdateUser, user))
    }


}