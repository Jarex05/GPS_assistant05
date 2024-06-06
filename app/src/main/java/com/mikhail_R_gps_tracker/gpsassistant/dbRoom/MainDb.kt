package com.mikhail_R_gps_tracker.gpsassistant.dbRoom

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database (entities = [TrackItem::class], version = 1)
abstract class MainDb : RoomDatabase(){

    abstract fun getDao(): Dao
    companion object{
        @Volatile
        var INSTANCE: MainDb? = null
        fun getDatabase(context: Context): MainDb{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDb::class.java,
                    "GpsAssistant1.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}