package net.group.androidlocalmessanger.network.client.controller

import android.content.Context
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.ui.chat.ChatViewModel
import net.group.androidlocalmessanger.ui.main.MainViewModule
import net.group.androidlocalmessanger.utils.Utils

object MainController {
    val groupIdToChatViewModel: HashMap<String, ChatViewModel> = hashMapOf()
    val mainViewModule = MainViewModule()

    suspend fun sendMessage(context: Context, message: Message) {

        if (message.attachedFileName != null) {
            val fileName = Utils.getFileName(message.attachedFileName)
            val filePath = message.attachedFileName.toString()
            message.attachedFileName = fileName
            Sender.sendRequest(OrderData(Order.SendMessage, message))
            Sender.uploadFile(fileName, filePath, context)
        } else
            Sender.sendRequest(OrderData(Order.SendMessage, message))

    }

    suspend fun createGroup(group: GroupWithUsers) {
        Sender.sendRequest(OrderData(Order.CreateGroup, group))
    }

    suspend fun sendUpdatedGroup(group: GroupWithUsers) {
        Sender.sendRequest(OrderData(Order.UpdateGroup, group))
    }


    fun refreshGroups(response: Response<*>) {
        val groups = response.data as List<GroupWithUsers>
        mainViewModule.setGroups(groups)
    }

    fun refreshUsers(context: Context, response: Response<*>) {
        val users = response.data as List<User>
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