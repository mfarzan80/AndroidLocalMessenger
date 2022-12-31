package net.group.androidlocalmessanger.data

import androidx.room.*
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.module.UserGroupCrossRef
import net.group.androidlocalmessanger.utils.ListTypeConverters


@androidx.room.Database(entities = [Group::class, User::class, UserGroupCrossRef::class], version = 1)
@TypeConverters(ListTypeConverters::class)
abstract class Database : RoomDatabase() {
    abstract fun dao(): DatabaseDao
}
