package net.group.androidlocalmessanger.repository

import net.group.androidlocalmessanger.data.DatabaseDao
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.module.UserWithGroups

class UserRepository(private val databaseDao: DatabaseDao) {

    suspend fun getUserByEmail(email: String): UserWithGroups? {
        return databaseDao.getUserByEmail(email)
    }

    suspend fun getUserByUsername(userName: String): User? {
        return databaseDao.getUserByUsername(userName)
    }

    suspend fun getAllUsers(): List<User> {
        return databaseDao.getAllUsers()
    }

    suspend fun insertUser(user: User) {
        databaseDao.insertUser(user)
    }

    suspend fun updateUser(updatedUser: User) {
        databaseDao.updateUser(updatedUser)
    }

}