package net.group.androidlocalmessanger.repository

import net.group.androidlocalmessanger.data.DatabaseDao
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.module.UserWithGroups

class UserRepository(private val databaseDao: DatabaseDao) {

    suspend fun getUserByUsername(userName: String): UserWithGroups? {
        return databaseDao.getUserByUsername(userName)
    }

    suspend fun getAllUsers(): List<User> {
        return databaseDao.getAllUsers()
    }

    suspend fun insertUser(user: User) {

        databaseDao.insertUser(user)
    }

}