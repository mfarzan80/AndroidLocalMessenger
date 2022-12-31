package net.group.androidlocalmessanger.ui.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.controller.ClientController
import net.group.androidlocalmessanger.network.client.controller.MainController

class MainViewModule : ViewModel() {

    val groups = mutableStateMapOf<String, GroupWithUsers>()
    val groupLoading = mutableStateOf(false)

    val users: MutableState<DataOrException<List<User>, Boolean, ResponseCode>> = mutableStateOf(
        DataOrException(listOf(), false, null)
    )

    fun sendGroupsRequest() {
        viewModelScope.launch {
            groupsLoading()
            MainController.sendGroupsRequest()
        }
    }

    fun sendUsersRequest() {
        viewModelScope.launch {
            usersLoading()
            MainController.sendUsersRequest()
        }

    }

    fun addGroup(groupWithUsers: GroupWithUsers) {
        viewModelScope.launch {
            if (!groups.contains(groupWithUsers.group.groupId)) {
                groupsLoading()
                MainController.createGroup(groupWithUsers)
            }
        }
    }


    fun setGroups(newGroups: List<GroupWithUsers>) {
        newGroups.forEach {
            groups[it.group.groupId] = it
        }
    }

    fun updateGroup(groupWithUsers: GroupWithUsers) {
        groups[groupWithUsers.group.groupId] = groupWithUsers
    }

    fun setUsers(dataOrException: DataOrException<List<User>, Boolean, ResponseCode>) {
        users.value = dataOrException
    }

    private fun usersLoading() {
        users.value = users.value.copy(loading = true)
    }

    private fun groupsLoading() {
        groupLoading.value = true
    }

    fun getPvGroup(user: User): GroupWithUsers {
        var groupWithUsers: GroupWithUsers? = null
        groups.values.forEach {
            if (it.group.type == Group.GROUP_TYPE_CHAT && it.users.contains(user)) {
                groupWithUsers = it
                return@forEach
            }
        }
        return if (groupWithUsers != null)
            groupWithUsers!!
        else {
            groupWithUsers = GroupWithUsers(
                Group(name = "Chat", Group.GROUP_TYPE_CHAT),
                listOf(user, ClientController.client.user!!)
            )
            addGroup(groupWithUsers!!)
            groupWithUsers!!
        }

    }


    fun getUser(): User {
        return ClientController.client.user!!
    }
    fun isOwnerUser(message: Message): Boolean {
        return getUser() == message.sender
    }
}