package net.group.androidlocalmessanger.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.User


@Dao
interface DatabaseDao {

    @Query("SELECT * FROM users WHERE `email` == :userName")
    suspend fun getUserByUsername(userName: String): User?

    @Insert(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(entity = Group::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Update(entity = Group::class)
    suspend fun updateGroup(group: Group)


    @Query("SELECT * FROM groups WHERE :id == id")
    suspend fun getGroupById(id: String): Group

    @Query("SELECT * FROM groups")
    fun getAllGroupsFlow(): Flow<List<Group>>

    @Query("SELECT * FROM groups")
    suspend fun getAllGroups(): List<Group>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}