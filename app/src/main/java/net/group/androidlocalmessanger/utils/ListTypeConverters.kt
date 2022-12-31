package net.group.androidlocalmessanger.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.group.androidlocalmessanger.module.Message


class ListTypeConverters {
    companion object {

        val gson = Gson()
    }

    @TypeConverter
    fun stringToArrayListString(string: String): ArrayList<String> {
        return ArrayList(gson.fromJson(string, List::class.java) as List<String>)
    }

    @TypeConverter
    fun arrayListToStringArrayList(arrayList: ArrayList<String>): String {
        return gson.toJson(arrayList)
    }

    @TypeConverter
    fun stringToMessageArrayList(string: String): ArrayList<Message> {
        return gson.fromJson(string, object : TypeToken<List<Message?>?>() {}.type)
    }

    @TypeConverter
    fun messageArrayListToString(arrayList: ArrayList<Message>): String {
        return gson.toJson(arrayList)
    }

}