package net.group.androidlocalmessanger.di

import android.content.Context
import androidx.room.Room
import net.group.androidlocalmessanger.data.Database
import net.group.androidlocalmessanger.data.DatabaseDao


object AppModule {
    var databaseDao: DatabaseDao? = null


    fun getDatabaseDao(context: Context): DatabaseDao {
        if (databaseDao == null)
            databaseDao = Room.databaseBuilder(context, Database::class.java, "messenger_db")
                .fallbackToDestructiveMigration().build().dao()
        return databaseDao!!
    }


}