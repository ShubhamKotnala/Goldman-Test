package com.goldman.test.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ApodResponse::class), version = 1, exportSchema = false)
abstract class ApodDatabase : RoomDatabase() {

    abstract fun apodDao(): ApodDao

    companion object {
        const val DB_NAME = "apod_database"
        @Volatile
        private var INSTANCE: ApodDatabase? = null

        fun getDatabase(context: Context): ApodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApodDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
