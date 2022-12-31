package net.group.androidlocalmessanger.repository

import net.group.androidlocalmessanger.data.DatabaseDao
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.GroupWithUsers
import net.group.androidlocalmessanger.module.UserGroupCrossRef


class GroupRepository(private val databaseDao: DatabaseDao) {

    suspend fun insertGroupWithUsers(groupWithUsers: GroupWithUsers) {
        databaseDao.insertGroup(groupWithUsers.group)
        groupWithUsers.users.forEach {
            databaseDao.insertUserGroupRel(
                UserGroupCrossRef(
                    it.userEmail,
                    groupWithUsers.group.groupId
                )
            )
        }
    }

    suspend fun getAllGroups(): List<GroupWithUsers> {
        return databaseDao.getAllGroupsWithUsers()
    }

    suspend fun getGroupById(id: String): GroupWithUsers? {
        return databaseDao.getGroupById(id)
    }

    suspend fun updateGroup(group: Group) {
        databaseDao.updateGroup(group)
    }
}