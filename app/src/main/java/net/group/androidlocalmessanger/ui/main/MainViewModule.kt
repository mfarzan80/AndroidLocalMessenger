package net.group.androidlocalmessanger.ui.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.client.controller.MainController
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class MainViewModule : ViewModel() {

    val groups: MutableState<DataOrException<List<Group>, Boolean, ResponseCode>> =
        mutableStateOf(
            DataOrException(mutableStateListOf(), false, null)
        )

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


    fun setGroups(dataOrException: DataOrException<List<Group>, Boolean, ResponseCode>) {
        groups.value = dataOrException
    }

    fun setUsers(dataOrException: DataOrException<List<User>, Boolean, ResponseCode>) {
        users.value = dataOrException
    }

    fun usersLoading() {
        users.value = users.value.copy(loading = true)
    }

    fun groupsLoading() {
        groups.value = groups.value.copy(loading = true)
    }

    fun getPvGroup(user: User): Group {
        var group: Group? = null
        groups.value.data!!.forEach {
            if (it.type == Group.GROUP_TYPE_CHAT && it.users.contains(user)) {
                group = it
                return@forEach
            }
        }
        return if (group != null)
            group!!
        else
            Group(name = "Chat", Group.GROUP_TYPE_CHAT)

    }
}