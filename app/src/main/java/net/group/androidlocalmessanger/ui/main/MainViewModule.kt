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
import java.io.File

class MainViewModule : ViewModel() {

    val groups = mutableStateMapOf<String, GroupWithUsers>()
    val groupLoading = mutableStateOf(true)

    val users: MutableState<DataOrException<List<User>, Boolean, ResponseCode>> =
        mutableStateOf(
            DataOrException(listOf(), true, null)
        )


    fun addGroup(groupWithUsers: GroupWithUsers) {
        viewModelScope.launch {
            if (!groups.contains(groupWithUsers.group.groupId)) {
                groupsLoading()
                MainController.createGroup(groupWithUsers)
            }
        }
    }


    fun setGroups(newGroups: List<GroupWithUsers>) {
        groupLoading.value = false
        newGroups.forEach {
            groups[it.group.groupId] = it
        }
    }

    fun sendUpdatedGroup(groupWithUsers: GroupWithUsers) {
        viewModelScope.launch {
            MainController.sendUpdatedGroup(groupWithUsers)
            updateGroup(groupWithUsers)
        }
    }



    fun updateGroup(groupWithUsers: GroupWithUsers) {
        groups[groupWithUsers.group.groupId] = groupWithUsers
    }

    fun setUsers(dataOrException: DataOrException<List<User>, Boolean, ResponseCode>) {
        users.value = dataOrException
    }


    private fun startUsersLoading() {
        users.value = users.value.copy(loading = true)
    }

    private fun stopUsersLoading() {
        users.value = users.value.copy(loading = false)
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


    companion object {
        fun getMe(): User {
            return ClientController.client.user!!
        }

        fun checkProfile(user: User): Boolean {
            return user.profilePath != null && File(user.profilePath!!).exists()
        }

        fun needToDownloadProfile(user: User): Boolean {
            return user.profilePath != null && !File(user.profilePath!!).exists()
        }


    }

}
