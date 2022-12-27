package net.group.androidlocalmessanger.network.client.controller

import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.ui.main.MainViewModule

object MainController {
    val mainViewModule = MainViewModule()

    suspend fun sendGroupsRequest() {
        Sender.sendRequest(orderData = OrderData(Order.GetMyGroups, null))
    }

    suspend fun sendUsersRequest() {
        Sender.sendRequest(orderData = OrderData(Order.GetAllUsers, null))
    }

    fun refreshGroups(response: Response<*>) {
        val groups = response.data as List<Group>
        val de = DataOrException(groups, false, response.code)
        mainViewModule.setGroups(de)
    }

    fun refreshUsers(response: Response<*>) {
        val users = response.data as List<User>
        val de = DataOrException(users, false, response.code)
        mainViewModule.setUsers(de)
    }


}