package net.group.androidlocalmessanger.data

import androidx.room.*
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.Message
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.utils.HashsetTypeConverters


@androidx.room.Database(entities = [Group::class, User::class], version = 1)
@TypeConverters(HashsetTypeConverters::class)
abstract class Database : RoomDatabase() {
    abstract fun dao(): DatabaseDao
}
