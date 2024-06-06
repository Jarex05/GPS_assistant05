package com.mikhail_R_gps_tracker.gpsassistant.dbRoom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [TrackItemNechet::class], version = 1)
abstract class MainDbNechet : RoomDatabase() {

    abstract fun getDaoNechet(): DaoNechet
    companion object{
        @Volatile
        var INSTANCE: MainDbNechet? = null
        fun getDatabaseNechet(context: Context): MainDbNechet{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDbNechet::class.java,
                    "GpsAssistantNechet.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}