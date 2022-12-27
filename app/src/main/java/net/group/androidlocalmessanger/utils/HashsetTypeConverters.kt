package net.group.androidlocalmessanger.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import net.group.androidlocalmessanger.module.Message
import net.group.androidlocalmessanger.module.User


class HashsetTypeConverters {
    val gson = Gson()

    @TypeConverter
    fun messageHashsetToString(hashSet: HashSet<Message>): String {
        return gson.toJson(hashSet)
    }

    @TypeConverter
    fun messageStringToHashset(string: String): HashSet<Message> {
        return gson.fromJson(string, HashSet::class.java) as HashSet<Message>
    }

    @TypeConverter
    fun userHashsetToString(hashSet: HashSet<User>): String {
        return gson.toJson(hashSet)
    }

    @TypeConverter
    fun userStringToHashset(string: String): HashSet<User> {
        return gson.fromJson(string, HashSet::class.java) as HashSet<User>
    }

}