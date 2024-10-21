package com.sf.contactmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sf.contactmanager.dao.ContactDao
import com.sf.contactmanager.model.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class ContactDatabase():RoomDatabase() {
    abstract fun contactDao():ContactDao

    companion object{
        @Volatile
        private var DATABASE : ContactDatabase? = null

        fun getDatabaseInstance(context:Context):ContactDatabase= DATABASE?: synchronized(this){
            Room.databaseBuilder(context.applicationContext,ContactDatabase::class.java,"app_db")
                .fallbackToDestructiveMigration()
                .build()
                .also {
                    DATABASE = it
                }
        }
    }
}