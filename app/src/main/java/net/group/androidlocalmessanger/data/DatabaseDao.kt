package net.group.androidlocalmessanger.data

import androidx.room.*
import net.group.androidlocalmessanger.module.*


@Dao
interface DatabaseDao {


    @Insert(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(entity = Group::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Insert(entity = UserGroupCrossRef::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserGroupRel(userGroupCrossRef: UserGroupCrossRef)

    @Update(entity = Group::class)
    suspend fun updateGroup(group: Group)

    @Update(entity = User::class)
    suspend fun updateUser(updatedUser: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>


    @Transaction
    @Query("SELECT * FROM users WHERE userEmail == :email")
    suspend fun getUserByEmail(email: String): UserWithGroups?

    @Query("SELECT * FROM users WHERE userName == :userName")
    suspend fun getUserByUsername(userName: String): User?

    @Transaction
    @Query("SELECT * FROM groups WHERE :id == groupId")
    suspend fun getGroupById(id: String): GroupWithUsers?

    @Transaction
    @Query("SELECT * FROM groups")
    suspend fun getAllGroupsWithUsers(): List<GroupWithUsers>

    @Transaction
    @Query("SELECT * FROM users")
    suspend fun getUserWithGroups(): List<UserWithGroups>


}