package com.example.counturu.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Counter::class, User::class], version = 5, exportSchema = false)
abstract class CounterDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: CounterDatabase? = null

        fun getDatabase(context: Context): CounterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CounterDatabase::class.java,
                    "counter_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

