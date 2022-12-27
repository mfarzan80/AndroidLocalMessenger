package net.group.androidlocalmessanger.repository

import net.group.androidlocalmessanger.data.DatabaseDao
import net.group.androidlocalmessanger.module.Group


class GroupRepository(private val databaseDao: DatabaseDao) {

    suspend fun insertGroup(group: Group) {
        databaseDao.insertGroup(group)
    }

    suspend fun getAllGroups(): List<Group> {
        return databaseDao.getAllGroups()
    }

}