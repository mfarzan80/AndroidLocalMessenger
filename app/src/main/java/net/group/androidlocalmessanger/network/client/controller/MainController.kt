package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.ui.chat.ChatViewModel
import net.group.androidlocalmessanger.ui.main.MainViewModule
import net.group.androidlocalmessanger.utils.Catcher

object MainController {
    val groupIdToChatViewModel: HashMap<String, ChatViewModel> = hashMapOf()
    val mainViewModule = MainViewModule()

    suspend fun sendGroupsRequest() {
        Sender.sendRequest(orderData = OrderData(Order.GetMyGroups, null))
    }

    suspend fun sendUsersRequest() {
        Sender.sendRequest(orderData = OrderData(Order.GetAllUsers, null))
    }

    suspend fun sendMessage(message: Message) {
        Sender.sendRequest(OrderData(Order.SendMessage, message))
    }

    suspend fun createGroup(group: GroupWithUsers) {
        Sender.sendRequest(OrderData(Order.CreateGroup, group))
    }

    fun refreshGroups(response: Response<*>) {
        val groups = response.data as List<GroupWithUsers>
        mainViewModule.setGroups(groups)
    }

    fun refreshUsers(context: Context, response: Response<*>) {
        val catcher = Catcher(context)
        val users = response.data as List<User>
        users.forEach { user ->
            if (user.profilePath != null) {
                if (catcher.getLocalPathByServerPath(user.profilePath) == null) {
                    val profileFile =
                        ClientReceiver.receiveFile(
                            context,
                            user.userEmail + "_ProfilePhoto" + System.nanoTime() + ".jpg"
                        )

                    catcher.saveLocalPath(user.profilePath, profileFile.path)

                }
            }
        }
        val de = DataOrException(users, false, response.code)
        mainViewModule.setUsers(de)
    }


    fun updateGroup(response: Response<*>) {
        val groupWithUsers = response.data as GroupWithUsers
        groupIdToChatViewModel[groupWithUsers.group.groupId]?.messages?.clear()
        groupIdToChatViewModel[groupWithUsers.group.groupId]?.messages?.addAll(groupWithUsers.group.messages)
        mainViewModule.updateGroup(groupWithUsers)
    }


}