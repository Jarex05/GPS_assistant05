package com.mikhail_R_gps_tracker.gpsassistant

import android.app.Application
import android.util.Log
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.MainDb
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.MainDbNechet
import com.yandex.mobile.ads.common.MobileAds

class MainApp : Application() {
    val database by lazy { MainDb.getDatabase(this) }
    val databaseNechet by lazy { MainDbNechet.getDatabaseNechet(this) }
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this){
            Log.d("MyLog", "Yandex Ads SDK initialized")
        }
    }
}