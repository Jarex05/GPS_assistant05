package com.mikhail_R_gps_tracker.gpsassistant.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.location.Location
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.mikhail_R_gps_tracker.gpsassistant.ActivityNechet
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.MyDbManagerPantograph
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.ViewTrackFragment
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.MainFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.MainFragmentNechet.Companion.LOC_MODEL_INTENT_FRAGMENT_NECHET
//import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.ViewTrackFragmentNechet.Companion.FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.lang.StringBuilder

@Suppress("DEPRECATION")
class LocationServiceNechet : Service() {
    private var sbL = StringBuilder()
    private var kmNechet: Int = 0
    private lateinit var geoPointsList: ArrayList<GeoPoint>

    private var distanceNechet = 10000000.0F
    private var mainDistanceNechet = 0.0F
    private var distanceNechet2 = 10000000.0F
    private var lastLocationNechet: Location? = null
    private lateinit var locProviderNechet: FusedLocationProviderClient
    private lateinit var locRequestNechet: LocationRequest
    private var isDebagNechet = true
    private lateinit var myDbManagerRedacktor: MyDbManagerRedacktor
    private var isRedaktorMinusNechet = true
    private var isRedaktorPlusNechet = true

    var kmDistanceNechet: Int = 0
    var pkDistanceNechet: Int = 0

    private var faktStartKmNechet: Int = 0
    private var faktFinishKmNechet: Int = 0

    private var titleStartRedacktorNechet: Int = 0
    private var piketStartRedacktorNechet: Int = 0
    private var faktNachKmRedacktorNechet: Int = 0
    private var piketNachKmRedacktorNechet: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val receiverNechet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentNechet: Intent?) {
            if (intentNechet?.action == LOC_MODEL_INTENT_FRAGMENT_NECHET){
                val fragmentModelNechet = intentNechet.getSerializableExtra(LOC_MODEL_INTENT_FRAGMENT_NECHET) as FragmentModelNechet
                mainDistanceNechet = fragmentModelNechet.mainDistanceNechet
                distanceNechet = fragmentModelNechet.mainDistanceNechet
                distanceNechet2 = fragmentModelNechet.mainDistanceNechet
                Log.d("MyLog", "distance: $distanceNechet")

            }
        }
    }

    private fun registerLocReceiverNechet(){
        val locFilterNechet = IntentFilter(LOC_MODEL_INTENT_FRAGMENT_NECHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverNechet, locFilterNechet)
    }

    private val receiverPlusNechet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentNechet: Intent?) {
            if (intentNechet?.action == MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_PLUS_NECHET){
                val fragmentModelPlusNechet = intentNechet.getSerializableExtra(MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_PLUS_NECHET) as FragmentPlusNechet
                distanceNechet += fragmentModelPlusNechet.savePlus
                Log.d("MyLog", "savePlus: $distanceNechet")


            }
        }
    }

    private fun registerLocReceiverPlusNechet(){
        val locFilterPlusNechet = IntentFilter(MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_PLUS_NECHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverPlusNechet, locFilterPlusNechet)
    }

    private val receiverMinusNechet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentNechet: Intent?) {
            if (intentNechet?.action == MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_MINUS_NECHET){
                val fragmentModelMinusNechet = intentNechet.getSerializableExtra(MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_MINUS_NECHET) as FragmentMinusNechet
                distanceNechet -= fragmentModelMinusNechet.saveMinus
                Log.d("MyLog", "savePlus: $distanceNechet")


            }
        }
    }

    private fun registerLocReceiverMinusNechet(){
        val locFilterMinusNechet = IntentFilter(MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_MINUS_NECHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverMinusNechet, locFilterMinusNechet)
    }

//    private val receiverLatLongKmToServiceNechet = object : BroadcastReceiver(){
//        override fun onReceive(context: Context?, intentLatLongKmToServiceNechet: Intent?) {
//            if (intentLatLongKmToServiceNechet?.action == FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET){
//                val fragmentLatLongKmToServiceNechet = intentLatLongKmToServiceNechet.getSerializableExtra(FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET) as FragmentLatLongKmToServiceNechet
//                if (isCheckTrackNechet && fragmentLatLongToServiceNechet == "") {
//                    fragmentLatLongToServiceNechet = fragmentLatLongKmToServiceNechet.fragmentLatLongKmToServiceNechet
//                    Log.d("MyLog", "FragmentLatLongKmToServiceNechet: $fragmentLatLongToServiceNechet")
//                    isCheckTrackNechet = !isCheckTrackNechet
//                }
//            }
//        }
//    }
//    private fun registerLatLongKmToServiceNechet(){
//        val locFilterLatLongKmToServiceNechet = IntentFilter(FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET)
//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(receiverLatLongKmToServiceNechet, locFilterLatLongKmToServiceNechet)
//    }

//    private fun getLatLongKm() {
//        if (distanceKmNechet.indices.isEmpty()) {
//            val list = fragmentLatLongToServiceNechet.split("/")
//            list.forEach {
//                if (it.isEmpty()) return@forEach
//                val points = it.split(",")
//                distanceKmNechet.add(ListLatLongKmNechet(points[0].toFloat(), points[1].toFloat(), points[2].toFloat()))
//            }
//        }
//    }
//
//    data class ListLatLongKmNechet(val lat: Float,
//                             val long: Float,
//                             val distKm: Float){
//        override fun toString(): String {
//            return super.toString()
//        }
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerLocReceiverNechet()
        registerLocReceiverPlusNechet()
        registerLocReceiverMinusNechet()
//        registerLatLongKmToServiceNechet()
        startNotificationNechet()
        startLocationUpdatesNechet()
        isRunningNechet = true
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        geoPointsList = ArrayList()
        initLocationNechet()

    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningNechet = false
        locProviderNechet.removeLocationUpdates(locCallbackNechet)
    }

//    if (currentLocationNechet.speed > 0.4 || isDebagNechet){
//        distanceBetweenPointsNechet = lastLocationNechet?.distanceTo(currentLocationNechet) ?: 0F
//        if (distanceBetweenPointsNechet > currentLocationNechet.accuracy){
//            distanceNechet -= distanceBetweenPointsNechet
//            distanceNechet2 -= distanceBetweenPointsNechet
//        }
//    }


//    if (lastLocationNechet != null && currentLocationNechet != null){
//        if (currentLocationNechet.speed > 0.4 || isDebagNechet){
//            distanceNechet -= lastLocationNechet?.distanceTo(currentLocationNechet)!!
//            distanceNechet2 -= lastLocationNechet?.distanceTo(currentLocationNechet)!!

    private val locCallbackNechet = object : LocationCallback(){
        override fun onLocationResult(lResultNechet: LocationResult) {
            super.onLocationResult(lResultNechet)
            val currentLocationNechet = lResultNechet.lastLocation
            if (lastLocationNechet != null && currentLocationNechet != null){
                if (currentLocationNechet.speed > 0.4 || isDebagNechet){
                    distanceNechet -= lastLocationNechet?.distanceTo(currentLocationNechet)!!
                    distanceNechet2 -= lastLocationNechet?.distanceTo(currentLocationNechet)!!
                    geoPointsList.add(GeoPoint(currentLocationNechet.latitude, currentLocationNechet.longitude))

                    if (mainDistanceNechet > 0) {
                        sbL.append("${currentLocationNechet.latitude}, ${currentLocationNechet.longitude}, $distanceNechet /")
                    } else {
                        sbL.append("${currentLocationNechet.latitude}, ${currentLocationNechet.longitude}, 0000000.0 /")
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        calculationKm()
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        myDbManagerRedacktor = MyDbManagerRedacktor(applicationContext)
                        myDbManagerRedacktor.openDb()
                        val dataListRedacktor = myDbManagerRedacktor.readDbDataRedacktorNechet()
                        for (item in dataListRedacktor){
                            titleStartRedacktorNechet = item.startNechet
                            piketStartRedacktorNechet = item.picketStartNechet

                            // Начало расчёта начала отнимания или прибавления метража по киллометро

                            var x = 9999999
                            var kmx = 10000
                            val pkx = 10
                            while (x > 0){
                                x -= 1000
                                kmx -= 1

                                if (titleStartRedacktorNechet == kmx && piketStartRedacktorNechet == pkx){
                                    faktNachKmRedacktorNechet = x
                                    piketNachKmRedacktorNechet = pkx
                                }
                            }

                            var x1 = 9999899
                            var kmx1 = 10000
                            val pkx1 = 9
                            while (x1 > 0){
                                x1 -= 1000
                                kmx1 -= 1

                                if (titleStartRedacktorNechet == kmx1 && piketStartRedacktorNechet == pkx1){
                                    faktNachKmRedacktorNechet = x1
                                    piketNachKmRedacktorNechet = pkx1
                                }
                            }

                            var x2 = 9999799
                            var kmx2 = 10000
                            val pkx2 = 8
                            while (x2 > 0){
                                x2 -= 1000
                                kmx2 -= 1

                                if (titleStartRedacktorNechet == kmx2 && piketStartRedacktorNechet == pkx2){
                                    faktNachKmRedacktorNechet = x2
                                    piketNachKmRedacktorNechet = pkx2
                                }
                            }

                            var x3 = 9999699
                            var kmx3 = 10000
                            val pkx3 = 7
                            while (x3 > 0){
                                x3 -= 1000
                                kmx3 -= 1

                                if (titleStartRedacktorNechet == kmx3 && piketStartRedacktorNechet == pkx3){
                                    faktNachKmRedacktorNechet = x3
                                    piketNachKmRedacktorNechet = pkx3
                                }
                            }

                            var x4 = 9999599
                            var kmx4 = 10000
                            val pkx4 = 6
                            while (x4 > 0){
                                x4 -= 1000
                                kmx4 -= 1

                                if (titleStartRedacktorNechet == kmx4 && piketStartRedacktorNechet == pkx4){
                                    faktNachKmRedacktorNechet = x4
                                    piketNachKmRedacktorNechet = pkx4
                                }
                            }

                            var x5 = 9999499
                            var kmx5 = 10000
                            val pkx5 = 5
                            while (x5 > 0){
                                x5 -= 1000
                                kmx5 -= 1

                                if (titleStartRedacktorNechet == kmx5 && piketStartRedacktorNechet == pkx5){
                                    faktNachKmRedacktorNechet = x5
                                    piketNachKmRedacktorNechet = pkx5
                                }
                            }

                            var x6 = 9999399
                            var kmx6 = 10000
                            val pkx6 = 4
                            while (x6 > 0){
                                x6 -= 1000
                                kmx6 -= 1

                                if (titleStartRedacktorNechet == kmx6 && piketStartRedacktorNechet == pkx6){
                                    faktNachKmRedacktorNechet = x6
                                    piketNachKmRedacktorNechet = pkx6
                                }
                            }

                            var x7 = 9999299
                            var kmx7 = 10000
                            val pkx7 = 3
                            while (x7 > 0){
                                x7 -= 1000
                                kmx7 -= 1

                                if (titleStartRedacktorNechet == kmx7 && piketStartRedacktorNechet == pkx7){
                                    faktNachKmRedacktorNechet = x7
                                    piketNachKmRedacktorNechet = pkx7
                                }
                            }

                            var x8 = 9999199
                            var kmx8 = 10000
                            val pkx8 = 2
                            while (x8 > 0){
                                x8 -= 1000
                                kmx8 -= 1

                                if (titleStartRedacktorNechet == kmx8 && piketStartRedacktorNechet == pkx8){
                                    faktNachKmRedacktorNechet = x8
                                    piketNachKmRedacktorNechet = pkx8
                                }
                            }

                            var x9 = 9999099
                            var kmx9 = 10000
                            val pkx9 = 1
                            while (x9 > 0){
                                x9 -= 1000
                                kmx9 -= 1

                                if (titleStartRedacktorNechet == kmx9 && piketStartRedacktorNechet == pkx9){
                                    faktNachKmRedacktorNechet = x9
                                    piketNachKmRedacktorNechet = pkx9
                                }
                            }

                            // Конец расчёта начала отнимания или прибавления метража по киллометро

                            // Начало отнимания метража

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 999 && distanceNechet2 >= faktNachKmRedacktorNechet + 951 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 949 && distanceNechet2 >= faktNachKmRedacktorNechet + 901 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 899 && distanceNechet2 >= faktNachKmRedacktorNechet + 851 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 849 && distanceNechet2 >= faktNachKmRedacktorNechet + 801 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 799 && distanceNechet2 >= faktNachKmRedacktorNechet + 751 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 749 && distanceNechet2 >= faktNachKmRedacktorNechet + 701 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 699 && distanceNechet2 >= faktNachKmRedacktorNechet + 651 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 649 && distanceNechet2 >= faktNachKmRedacktorNechet + 601 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 599 && distanceNechet2 >= faktNachKmRedacktorNechet + 551 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 549 && distanceNechet2 >= faktNachKmRedacktorNechet + 501 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 499 && distanceNechet2 >= faktNachKmRedacktorNechet + 451 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 449 && distanceNechet2 >= faktNachKmRedacktorNechet + 401 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 399 && distanceNechet2 >= faktNachKmRedacktorNechet + 351 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 349 && distanceNechet2 >= faktNachKmRedacktorNechet + 301 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 299 && distanceNechet2 >= faktNachKmRedacktorNechet + 251 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 249 && distanceNechet2 >= faktNachKmRedacktorNechet + 201 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 199 && distanceNechet2 >= faktNachKmRedacktorNechet + 151 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 149 && distanceNechet2 >= faktNachKmRedacktorNechet + 101 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 99 && distanceNechet2 >= faktNachKmRedacktorNechet + 51 && item.minusNechet != "" && isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet + 49 && distanceNechet2 >= faktNachKmRedacktorNechet + 1 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                val minusNechet = item.minusNechet.toInt() / 20
                                distanceNechet += minusNechet
                                isRedaktorMinusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet && distanceNechet >= faktNachKmRedacktorNechet - 49 && item.minusNechet != "" && isRedaktorMinusNechet){
                                distanceNechet2 = distanceNechet
                                isRedaktorMinusNechet = !isRedaktorMinusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet - 51 && distanceNechet >= faktNachKmRedacktorNechet - 99 && item.minusNechet != "" && !isRedaktorMinusNechet){
                                isRedaktorMinusNechet = true
                            }

                            // Конец отнимания метража

                            // Начало прибавления метража

                            if (distanceNechet <= faktNachKmRedacktorNechet + 999 && distanceNechet >= faktNachKmRedacktorNechet + 951 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 949 && distanceNechet >= faktNachKmRedacktorNechet + 901 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 899 && distanceNechet >= faktNachKmRedacktorNechet + 851 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 849 && distanceNechet >= faktNachKmRedacktorNechet + 801 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 799 && distanceNechet >= faktNachKmRedacktorNechet + 751 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 749 && distanceNechet >= faktNachKmRedacktorNechet + 701 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 699 && distanceNechet >= faktNachKmRedacktorNechet + 651 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 649 && distanceNechet >= faktNachKmRedacktorNechet + 601 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 599 && distanceNechet >= faktNachKmRedacktorNechet + 551 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 549 && distanceNechet >= faktNachKmRedacktorNechet + 501 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 499 && distanceNechet >= faktNachKmRedacktorNechet + 451 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 449 && distanceNechet >= faktNachKmRedacktorNechet + 401 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 399 && distanceNechet >= faktNachKmRedacktorNechet + 351 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 349 && distanceNechet >= faktNachKmRedacktorNechet + 301 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 299 && distanceNechet >= faktNachKmRedacktorNechet + 251 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 249 && distanceNechet >= faktNachKmRedacktorNechet + 201 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 199 && distanceNechet >= faktNachKmRedacktorNechet + 151 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 149 && distanceNechet >= faktNachKmRedacktorNechet + 101 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmRedacktorNechet + 99 && distanceNechet >= faktNachKmRedacktorNechet + 51 && item.plusNechet != "" && isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet <= faktNachKmRedacktorNechet + 49 && distanceNechet >= faktNachKmRedacktorNechet + 1 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                val plusNechet = item.plusNechet.toInt() / 20
                                distanceNechet -= plusNechet
                                isRedaktorPlusNechet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet2 <= faktNachKmRedacktorNechet && distanceNechet2 >= faktNachKmRedacktorNechet - 49 && item.plusNechet != "" && isRedaktorPlusNechet){
                                distanceNechet2 = distanceNechet
                                isRedaktorPlusNechet = !isRedaktorPlusNechet
                            }
                            if (distanceNechet2 <= faktNachKmRedacktorNechet - 51 && distanceNechet2 >= faktNachKmRedacktorNechet - 99 && item.plusNechet != "" && !isRedaktorPlusNechet){
                                isRedaktorPlusNechet = true
                            }

                            // Конец прибавления метража

                        }
                        myDbManagerRedacktor.closeDb()
                    }
                }
//                if (currentLocationNechet.speed > 0.4 || isDebagNechet){
//                    distanceBetweenPointsNechet = lastLocationNechet?.distanceTo(currentLocationNechet) ?: 0F
//                    if (distanceBetweenPointsNechet > currentLocationNechet.accuracy){
//                        distanceNechet -= distanceBetweenPointsNechet
//                        distanceNechet2 -= distanceBetweenPointsNechet
//                    }
//
//                    CoroutineScope(Dispatchers.Main).launch {
//                        calculationKm()
//                    }
//                }
                val locModelNechet = LocationModelNechet(
                    currentLocationNechet.speed,
                    kmNechet.toFloat(),
                    sbL,
                    distanceNechet,
                    distanceNechet2,
                    geoPointsList,
                    kmDistanceNechet,
                    pkDistanceNechet
                )
                sendLocDataNechet(locModelNechet)
            }
            lastLocationNechet = currentLocationNechet
        }
    }

    private fun sendLocDataNechet(locModelNechet: LocationModelNechet){
        val iNechet = Intent(LOC_MODEL_INTENT_NECHET)
        iNechet.putExtra(LOC_MODEL_INTENT_NECHET, locModelNechet)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(iNechet)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startNotificationNechet(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nChannelNechet = NotificationChannel(
                CHANNEL_ID_NECHET,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManagerNechet = getSystemService(NotificationManager::class.java) as NotificationManager
            nManagerNechet.createNotificationChannel(nChannelNechet)
        }
        val nIntentNechet = Intent(this, ActivityNechet::class.java)
        val pIntentNechet = PendingIntent.getActivity(
            this,
            12,
            nIntentNechet,
            PendingIntent.FLAG_MUTABLE
        )
        val notificationNechet = NotificationCompat.Builder(
            this,
            CHANNEL_ID_NECHET
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("GPS assistant Нечётный запущен!")
            .setContentIntent(pIntentNechet).build()
        startForeground(82, notificationNechet)

    }

    private fun initLocationNechet(){
        locRequestNechet = LocationRequest.create()
        locRequestNechet.interval = 1000
        locRequestNechet.fastestInterval = 1000
        locRequestNechet.priority = PRIORITY_HIGH_ACCURACY
        locProviderNechet = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    private fun startLocationUpdatesNechet(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) return

        locProviderNechet.requestLocationUpdates(
            locRequestNechet,
            locCallbackNechet,
            Looper.myLooper()
        )
    }

    private suspend fun calculationKm() = withContext(Dispatchers.IO){
        var q = 9999001
        var q1 = 9999101
        var q2 = 9999201
        var q3 = 9999301
        var q4 = 9999401
        var q5 = 9999501
        var q6 = 9999601
        var q7 = 9999701
        var q8 = 9999801
        var q9 = 9999901

        var w = 9999099
        var w1 = 9999199
        var w2 = 9999299
        var w3 = 9999399
        var w4 = 9999499
        var w5 = 9999599
        var w6 = 9999699
        var w7 = 9999799
        var w8 = 9999899
        var w9 = 9999999

        var kme = 10000
        var pke = 1

        while (w9 > 0) {
            q -= 1000
            q1 -= 1000
            q2 -= 1000
            q3 -= 1000
            q4 -= 1000
            q5 -= 1000
            q6 -= 1000
            q7 -= 1000
            q8 -= 1000
            q9 -= 1000

            w -= 1000
            w1 -= 1000
            w2 -= 1000
            w3 -= 1000
            w4 -= 1000
            w5 -= 1000
            w6 -= 1000
            w7 -= 1000
            w8 -= 1000
            w9 -= 1000

            kme -= 1

            if (distanceNechet <= w9 && distanceNechet >= q9){
                pke = 10
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w9
                faktFinishKmNechet = q9
            }

            if (distanceNechet <= w8 && distanceNechet >= q8){
                pke = 9
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w8
                faktFinishKmNechet = q8
            }

            if (distanceNechet <= w7 && distanceNechet >= q7){
                pke = 8
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w7
                faktFinishKmNechet = q7
            }

            if (distanceNechet <= w6 && distanceNechet >= q6){
                pke = 7
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w6
                faktFinishKmNechet = q6
            }

            if (distanceNechet <= w5 && distanceNechet >= q5){
                pke = 6
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w5
                faktFinishKmNechet = q5
            }

            if (distanceNechet <= w4 && distanceNechet >= q4){
                pke = 5
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w4
                faktFinishKmNechet = q4
            }

            if (distanceNechet <= w3 && distanceNechet >= q3){
                pke = 4
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w3
                faktFinishKmNechet = q3
            }

            if (distanceNechet <= w2 && distanceNechet >= q2){
                pke = 3
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w2
                faktFinishKmNechet = q2
            }

            if (distanceNechet <= w1 && distanceNechet >= q1){
                pke = 2
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w1
                faktFinishKmNechet = q1
            }

            if (distanceNechet <= w && distanceNechet >= q){
                pke = 1
                kmDistanceNechet = kme
                pkDistanceNechet = pke

                faktStartKmNechet = w
                faktFinishKmNechet = q
            }
        }
    }

    companion object{
        const val LOC_MODEL_INTENT_NECHET = "loc_intent_nechet"
        const val CHANNEL_ID_NECHET = "channel_nechet_1"
        var isRunningNechet = false
//        var startTimeNechet = 0L
    }
}