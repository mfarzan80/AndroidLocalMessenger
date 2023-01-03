package net.group.androidlocalmessanger.network.server.controller

import net.group.androidlocalmessanger.module.*
import net.group.androidlocalmessanger.network.server.ClientHandler
import net.group.androidlocalmessanger.network.server.ServerService

class ServerGroupsController(private val clientHandler: ClientHandler) {

    private val groupRepository = clientHandler.groupRepository

    suspend fun loadGroupsAndSend() {
        val groups = groupRepository.getAllGroups()
        sendUserGroups(groups)
    }

    private suspend fun sendUserGroups(groups: List<GroupWithUsers>) {
        clientHandler.sendResponse(Response(ResponseCode.OK, ResponseType.AllGroups,
            groups.filter {
                if (it.users.contains(clientHandler.client.user))
                    return@filter true
                return@filter false
            }
        ))
    }

    private suspend fun sendAGroupToItsUsers(group: GroupWithUsers) {
        group.users.forEach {
            val client = ServerService.userToClient[it]
            if (client != null && client != clientHandler)
                client.groupsController.sendAGroup(group)
        }
    }

    private suspend fun sendAGroup(group: GroupWithUsers) {
        clientHandler.sendResponse(
            Response(
                code = ResponseCode.OK,
                responseType = ResponseType.UpdatedGroup,
                data = group
            )
        )
    }


    suspend fun sendMessage(orderData: OrderData<*>) {
        val message = orderData.data as Message
        val groupWithUsers = groupRepository.getGroupById(message.groupId)
        if (message.attachedFileName != null) {
            clientHandler.receiveFile(message.attachedFileName!!)
        }
        if (groupWithUsers != null) {
            groupWithUsers.group.messages.add(message)
            groupRepository.updateGroup(groupWithUsers.group)
            sendAGroupToItsUsers(groupWithUsers)
        }
    }

    suspend fun addGroup(orderData: OrderData<*>) {
        addGroup(orderData.data as GroupWithUsers)
    }

    private suspend fun addGroup(groupWithUsers: GroupWithUsers) {
        groupRepository.insertGroupWithUsers(groupWithUsers)
        val groups = groupRepository.getAllGroups()
        groupWithUsers.users.forEach {
            ServerService.userToClient[it]?.groupsController?.sendUserGroups(groups)
        }
    }

    suspend fun updateGroupWithUsers(orderData: OrderData<*>) {
        val clientGroup = orderData.data as GroupWithUsers
        val serverGroup = groupRepository.getGroupById(clientGroup.group.groupId)
        var removedUser: User? = null
        var addedUsers: ArrayList<User>? = null
        if (clientGroup.users.size < serverGroup!!.users.size) { // remove user
            clientGroup.users.forEach {
                if (!serverGroup.users.contains(it)) {
                    removedUser = it
                    return@forEach
                }
            }
        } else if (clientGroup.users.size > serverGroup.users.size) {
            groupRepository.insertOrUpdateGroupUsers(clientGroup)
            addedUsers = ArrayList()
            clientGroup.users.forEach {
                if (!serverGroup.users.contains(it)) {
                    addedUsers.add(it)
                }
            }
        }
        groupRepository.updateGroup(clientGroup.group)
        if (removedUser != null) {
            groupRepository.removeUserFromGroup(removedUser!!, group = serverGroup.group)
            ServerService.userToClient[removedUser]!!.groupsController.loadGroupsAndSend()
        } else if (addedUsers != null) {
            val allGroups = groupRepository.getAllGroups()
            addedUsers.forEach {
                ServerService.userToClient[it]!!.groupsController.sendUserGroups(allGroups)
            }
        } else
            sendAGroupToItsUsers(clientGroup)
    }

}