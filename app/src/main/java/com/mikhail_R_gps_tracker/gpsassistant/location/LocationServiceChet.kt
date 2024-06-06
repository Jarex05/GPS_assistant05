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
import android.database.sqlite.SQLiteDatabase
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
import com.mikhail_R_gps_tracker.gpsassistant.ActivityChet
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.MyDbManagerPantograph
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.MainFragmentChet.Companion.LOC_MODEL_INTENT_FRAGMENT_CHET
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.MainFragmentChet.Companion.LOC_MODEL_INTENT_FRAGMENT_MINUS_CHET
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.MainFragmentChet.Companion.LOC_MODEL_INTENT_FRAGMENT_PLUS_CHET
//import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.ViewTrackFragment.Companion.FRAGMENT_LAT_LONG_KM_TO_SERVICE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.lang.StringBuilder

@Suppress("DEPRECATION", "UNREACHABLE_CODE")
class LocationServiceChet : Service() {
    private var sbL = StringBuilder()
    private var kmChet: Int = 0
//    private var kmChet4: Int = 0
    private lateinit var geoPointsList: ArrayList<GeoPoint>

    private var distanceChet = 0.0f
    private var mainDistanceChet = 0.0f
    private var distanceChet2 = 0.0F
    private var lastLocationChet: Location? = null
    private lateinit var locProviderChet: FusedLocationProviderClient
    private lateinit var locRequestChet: LocationRequest
    private var isDebagChet = true
    private lateinit var myDbManagerRedacktor: MyDbManagerRedacktor
    private var isRedacktorMinusChet = true
    private var isRedacktorPlusChet = true

    var kmDistanceChet: Int = 0
    var pkDistanceChet: Int = 0

    private var faktStartKmChet: Int = 0
    private var faktFinishKmChet: Int = 0

    private var titleStartRedacktorChet: Int = 0
    private var piketStartRedacktorChet: Int = 0
    private var faktNachKmRedacktorChet: Int = 0
    private var piketNachKmRedacktorChet: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val receiverChet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentChet: Intent?) {
            if (intentChet?.action == LOC_MODEL_INTENT_FRAGMENT_CHET){
                val fragmentModelChet = intentChet.getSerializableExtra(LOC_MODEL_INTENT_FRAGMENT_CHET) as FragmentModelChet
                mainDistanceChet = fragmentModelChet.mainDistanceChet
                distanceChet = fragmentModelChet.mainDistanceChet
                distanceChet2 = fragmentModelChet.mainDistanceChet
                Log.d("MyLog", "distance: $distanceChet")


            }
        }
    }

    private fun registerLocReceiverChet(){
        val locFilterChet = IntentFilter(LOC_MODEL_INTENT_FRAGMENT_CHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverChet, locFilterChet)
    }

    private val receiverPlusChet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentChet: Intent?) {
            if (intentChet?.action == LOC_MODEL_INTENT_FRAGMENT_PLUS_CHET){
                val fragmentModelPlusChet = intentChet.getSerializableExtra(LOC_MODEL_INTENT_FRAGMENT_PLUS_CHET) as FragmentPlusChet
                distanceChet += fragmentModelPlusChet.savePlus
                Log.d("MyLog", "savePlus: $distanceChet")


            }
        }
    }

    private fun registerLocReceiverPlusChet(){
        val locFilterPlusChet = IntentFilter(LOC_MODEL_INTENT_FRAGMENT_PLUS_CHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverPlusChet, locFilterPlusChet)
    }

    private val receiverMinusChet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentChet: Intent?) {
            if (intentChet?.action == LOC_MODEL_INTENT_FRAGMENT_MINUS_CHET){
                val fragmentModelMinusChet = intentChet.getSerializableExtra(LOC_MODEL_INTENT_FRAGMENT_MINUS_CHET) as FragmentMinusChet
                distanceChet -= fragmentModelMinusChet.saveMinus
                Log.d("MyLog", "savePlus: $distanceChet")


            }
        }
    }

    private fun registerLocReceiverMinusChet(){
        val locFilterMinusChet = IntentFilter(LOC_MODEL_INTENT_FRAGMENT_MINUS_CHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverMinusChet, locFilterMinusChet)
    }

//    private val receiverLatLongKmToService = object : BroadcastReceiver(){
//        override fun onReceive(context: Context?, intentLatLongKmToService: Intent?) {
//            if (intentLatLongKmToService?.action == FRAGMENT_LAT_LONG_KM_TO_SERVICE){
//                val fragmentLatLongKmToService = intentLatLongKmToService.getSerializableExtra(FRAGMENT_LAT_LONG_KM_TO_SERVICE) as FragmentLatLongKmToService
//                if (isCheckTrackNechet && fragmentLatLongToService == "") {
//                    fragmentLatLongToService = fragmentLatLongKmToService.fragmentLatLongKmToService
//                    Log.d("MyLog", "FragmentLatLongKmToService: $fragmentLatLongToService")
//                    isCheckTrackNechet = !isCheckTrackNechet
//                }
//            }
//        }
//    }
//    private fun registerLatLongKmToService(){
//        val locFilterLatLongKmToService = IntentFilter(FRAGMENT_LAT_LONG_KM_TO_SERVICE)
//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(receiverLatLongKmToService, locFilterLatLongKmToService)
//    }
//    private fun getLatLongKm() {
//        if (distanceKm.indices.isEmpty()) {
//            val list = fragmentLatLongToService.split("/")
//            list.forEach {
//                if (it.isEmpty()) return@forEach
//                val points = it.split(",")
//                distanceKm.add(ListLatLongKm(points[0].toFloat(), points[1].toFloat(), points[2].toFloat()))
//            }
//        }
//    }
//
//    data class ListLatLongKm(val lat: Float,
//                    val long: Float,
//                    val distKm: Float){
//        override fun toString(): String {
//            return super.toString()
//        }
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerLocReceiverChet()
        registerLocReceiverPlusChet()
        registerLocReceiverMinusChet()
//        registerLatLongKmToService()
        startNotificationChet()
        startLocationUpdatesChet()
        isRunningChet = true
//        isRedaktor2 = true
        return START_STICKY
//        val mainDistance = intent?.getStringExtra("mainDistanceChet")
//        Log.d("MyLog", "mainDistance: $mainDistance")
//        kmChet4 = mainDistance!!.toInt()
//        Log.d("MyLog", "kmChet4: $kmChet4")
    }

    override fun onCreate() {
        super.onCreate()
        geoPointsList = ArrayList()
        initLocationChet()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningChet = false
        locProviderChet.removeLocationUpdates(locCallbackChet)
    }

    private val locCallbackChet = object : LocationCallback(){
        override fun onLocationResult(lResultChet: LocationResult) {
            super.onLocationResult(lResultChet)
            val currentLocationChet = lResultChet.lastLocation
            if (lastLocationChet != null && currentLocationChet != null){
                if (currentLocationChet.speed > 0.4 || isDebagChet){
                    distanceChet += lastLocationChet?.distanceTo(currentLocationChet)!!
                    distanceChet2 += lastLocationChet?.distanceTo(currentLocationChet)!!
                    geoPointsList.add(GeoPoint(currentLocationChet.latitude, currentLocationChet.longitude))

//                    Log.d("MyLog", "kmChet4: $kmChet4")

                    if (mainDistanceChet > 0){
                        sbL.append("${currentLocationChet.latitude}, ${currentLocationChet.longitude}, $distanceChet /")
                    } else {
                        sbL.append("${currentLocationChet.latitude}, ${currentLocationChet.longitude}, 0000000.0 /")
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        calculationKm()
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        myDbManagerRedacktor = MyDbManagerRedacktor(applicationContext)
                        myDbManagerRedacktor.openDb()
                        val dataListRedacktor = myDbManagerRedacktor.readDbDataRedacktorChet()
                        for (item in dataListRedacktor){
                            titleStartRedacktorChet = item.startChet
                            piketStartRedacktorChet = item.picketStartChet

                            // Начало расчёта начала отнимания или прибавления метража по киллометро

                            var z = 1001
                            var kms = 2
                            val pkc = 1

                            while (z < 9999999){
                                z += 1000
                                kms += 1

                                if (titleStartRedacktorChet == kms && piketStartRedacktorChet == pkc){
                                    faktNachKmRedacktorChet = z
                                    piketNachKmRedacktorChet = pkc
                                }
                            }

                            var z1 = 1101
                            var kms1 = 2
                            val pkc1 = 2

                            while (z1 < 9999999){
                                z1 += 1000
                                kms1 += 1

                                if (titleStartRedacktorChet == kms1 && piketStartRedacktorChet == pkc1){
                                    faktNachKmRedacktorChet = z1
                                    piketNachKmRedacktorChet = pkc1
                                }
                            }

                            var z2 = 1201
                            var kms2 = 2
                            val pkc2 = 3

                            while (z2 < 9999999){
                                z2 += 1000
                                kms2 += 1

                                if (titleStartRedacktorChet == kms2 && piketStartRedacktorChet == pkc2){
                                    faktNachKmRedacktorChet = z2
                                    piketNachKmRedacktorChet = pkc2
                                }
                            }

                            var z3 = 1301
                            var kms3 = 2
                            val pkc3 = 4

                            while (z3 < 9999999){
                                z3 += 1000
                                kms3 += 1

                                if (titleStartRedacktorChet == kms3 && piketStartRedacktorChet == pkc3){
                                    faktNachKmRedacktorChet = z3
                                    piketNachKmRedacktorChet = pkc3
                                }
                            }

                            var z4 = 1401
                            var kms4 = 2
                            val pkc4 = 5

                            while (z4 < 9999999){
                                z4 += 1000
                                kms4 += 1

                                if (titleStartRedacktorChet == kms4 && piketStartRedacktorChet == pkc4){
                                    faktNachKmRedacktorChet = z4
                                    piketNachKmRedacktorChet = pkc4
                                }
                            }

                            var z5 = 1501
                            var kms5 = 2
                            val pkc5 = 6

                            while (z5 < 9999999){
                                z5 += 1000
                                kms5 += 1

                                if (titleStartRedacktorChet == kms5 && piketStartRedacktorChet == pkc5){
                                    faktNachKmRedacktorChet = z5
                                    piketNachKmRedacktorChet = pkc5
                                }
                            }

                            var z6 = 1601
                            var kms6 = 2
                            val pkc6 = 7

                            while (z6 < 9999999){
                                z6 += 1000
                                kms6 += 1

                                if (titleStartRedacktorChet == kms6 && piketStartRedacktorChet == pkc6){
                                    faktNachKmRedacktorChet = z6
                                    piketNachKmRedacktorChet = pkc6
                                }
                            }

                            var z7 = 1701
                            var kms7 = 2
                            val pkc7 = 8

                            while (z7 < 9999999){
                                z7 += 1000
                                kms7 += 1

                                if (titleStartRedacktorChet == kms7 && piketStartRedacktorChet == pkc7){
                                    faktNachKmRedacktorChet = z7
                                    piketNachKmRedacktorChet = pkc7
                                }
                            }

                            var z8 = 1801
                            var kms8 = 2
                            val pkc8 = 9

                            while (z8 < 9999999){
                                z8 += 1000
                                kms8 += 1

                                if (titleStartRedacktorChet == kms8 && piketStartRedacktorChet == pkc8){
                                    faktNachKmRedacktorChet = z8
                                    piketNachKmRedacktorChet = pkc8
                                }
                            }

                            var z9 = 1901
                            var kms9 = 2
                            val pkc9 = 10

                            while (z9 < 9999999){
                                z9 += 1000
                                kms9 += 1

                                if (titleStartRedacktorChet == kms9 && piketStartRedacktorChet == pkc9){
                                    faktNachKmRedacktorChet = z9
                                    piketNachKmRedacktorChet = pkc9
                                }
                            }

                            // Конец расчёта начала отнимания или прибавления метража по киллометро

                            // Начало отнимания метража

                            if (distanceChet2 >= faktNachKmRedacktorChet - 999 && distanceChet2 <= faktNachKmRedacktorChet - 951 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 949 && distanceChet2 <= faktNachKmRedacktorChet - 901 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 899 && distanceChet2 <= faktNachKmRedacktorChet - 851 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 849 && distanceChet2 <= faktNachKmRedacktorChet - 801 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 799 && distanceChet2 <= faktNachKmRedacktorChet - 751 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 749 && distanceChet2 <= faktNachKmRedacktorChet - 701 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 699 && distanceChet2 <= faktNachKmRedacktorChet - 651 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 649 && distanceChet2 <= faktNachKmRedacktorChet - 601 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 599 && distanceChet2 <= faktNachKmRedacktorChet - 551 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 549 && distanceChet2 <= faktNachKmRedacktorChet - 501 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 499 && distanceChet2 <= faktNachKmRedacktorChet - 451 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 449 && distanceChet2 <= faktNachKmRedacktorChet - 401 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 399 && distanceChet2 <= faktNachKmRedacktorChet - 351 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 349 && distanceChet2 <= faktNachKmRedacktorChet - 301 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 299 && distanceChet2 <= faktNachKmRedacktorChet - 251 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 249 && distanceChet2 <= faktNachKmRedacktorChet - 201 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 199 && distanceChet2 <= faktNachKmRedacktorChet - 151 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 149 && distanceChet2 <= faktNachKmRedacktorChet - 101 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 99 && distanceChet2 <= faktNachKmRedacktorChet - 51 && item.minusChet != "" && isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 49 && distanceChet2 <= faktNachKmRedacktorChet - 1 && item.minusChet != "" && !isRedacktorMinusChet){
                                val minusChet = item.minusChet.toInt() / 20
                                distanceChet -= minusChet
                                isRedacktorMinusChet = true
                            }
                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmRedacktorChet && distanceChet <= faktNachKmRedacktorChet + 49 && item.minusChet != "" && isRedacktorMinusChet){
                                distanceChet2 = distanceChet
                                isRedacktorMinusChet = !isRedacktorMinusChet
                            }
                            if (distanceChet >= faktNachKmRedacktorChet + 51 && distanceChet <= faktNachKmRedacktorChet + 99 && item.minusChet != "" && !isRedacktorMinusChet){
                                isRedacktorMinusChet = true
                            }

                            // Конец отнимания метража

                            // Начало прибавления метража

                            if (distanceChet2 >= faktNachKmRedacktorChet - 999 && distanceChet2 <= faktNachKmRedacktorChet - 951 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 949 && distanceChet2 <= faktNachKmRedacktorChet - 901 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 899 && distanceChet2 <= faktNachKmRedacktorChet - 851 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 849 && distanceChet2 <= faktNachKmRedacktorChet - 801 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 799 && distanceChet2 <= faktNachKmRedacktorChet - 751 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 749 && distanceChet2 <= faktNachKmRedacktorChet - 701 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 699 && distanceChet2 <= faktNachKmRedacktorChet - 651 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 649 && distanceChet2 <= faktNachKmRedacktorChet - 601 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 599 && distanceChet2 <= faktNachKmRedacktorChet - 551 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 549 && distanceChet2 <= faktNachKmRedacktorChet - 501 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 499 && distanceChet2 <= faktNachKmRedacktorChet - 451 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 449 && distanceChet2 <= faktNachKmRedacktorChet - 401 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 399 && distanceChet2 <= faktNachKmRedacktorChet - 351 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 349 && distanceChet2 <= faktNachKmRedacktorChet - 301 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 299 && distanceChet2 <= faktNachKmRedacktorChet - 251 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 249 && distanceChet2 <= faktNachKmRedacktorChet - 201 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 199 && distanceChet2 <= faktNachKmRedacktorChet - 151 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 149 && distanceChet2 <= faktNachKmRedacktorChet - 101 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet - 99 && distanceChet2 <= faktNachKmRedacktorChet - 51 && item.plusChet != "" && isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet - 49 && distanceChet2 <= faktNachKmRedacktorChet - 1 && item.plusChet != "" && !isRedacktorPlusChet){
                                val plusChet = item.plusChet.toInt() / 20
                                distanceChet += plusChet
                                isRedacktorPlusChet = true
                            }
                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet2 >= faktNachKmRedacktorChet && distanceChet2 <= faktNachKmRedacktorChet + 49 && item.plusChet != "" && isRedacktorPlusChet){
                                distanceChet2 = distanceChet
                                isRedacktorPlusChet = !isRedacktorPlusChet
                            }
                            if (distanceChet2 >= faktNachKmRedacktorChet + 51 && distanceChet <= faktNachKmRedacktorChet + 99 && item.plusChet != "" && !isRedacktorPlusChet){
                                isRedacktorPlusChet = true
                            }

                            // Конец прибавления метража
                        }
                        myDbManagerRedacktor.closeDb()
                    }


                }

//                if (currentLocationChet.speed > 0.4 || isDebagChet){
//                    distanceBetweenPointsChet = lastLocationChet?.distanceTo(currentLocationChet) ?: 0F
//                    if (distanceBetweenPointsChet > currentLocationChet.accuracy){
//                        distanceChet += distanceBetweenPointsChet
//                        distanceChet2 += distanceBetweenPointsChet
//                    }
//
//                    CoroutineScope(Dispatchers.Main).launch {
//                        calculationKm()
//                    }
//
////                    CoroutineScope(Dispatchers.IO).launch {
////                        startRedaktorKm()
////                    }
////
////                    startIfinishLimitationsKm()
//
//                }

//                if (currentLocationChet.speed > 0.4 || isDebagChet){
//                    distanceChet += lastLocationChet?.distanceTo(currentLocationChet)!!
//                    distanceChet2 += lastLocationChet?.distanceTo(currentLocationChet)!!
//
//                    CoroutineScope(Dispatchers.Main).launch {
//                        calculationKm()
//                    }
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        startRedaktorKm()
//                    }
//
//                    startIfinishLimitationsKm()
//
//                }

                val locModelChet = LocationModelChet(
                    currentLocationChet.speed,
                    kmChet.toFloat(),
                    sbL,
                    distanceChet,
                    distanceChet2,
                    geoPointsList,
                    kmDistanceChet,
                    pkDistanceChet,
                )
                sendLocDataChet(locModelChet)
            }
            lastLocationChet = currentLocationChet
        }
    }



    private fun sendLocDataChet(locModelChet: LocationModelChet){
        val iChet = Intent(LOC_MODEL_INTENT_CHET)
        iChet.putExtra(LOC_MODEL_INTENT_CHET, locModelChet)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(iChet)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startNotificationChet(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nChannelChet = NotificationChannel(
                CHANNEL_ID_CHET,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManagerChet = getSystemService(NotificationManager::class.java) as NotificationManager
            nManagerChet.createNotificationChannel(nChannelChet)
        }
        val nIntentChet = Intent(this, ActivityChet::class.java)
        val pIntentChet = PendingIntent.getActivity(
            this,
            11,
            nIntentChet,
            PendingIntent.FLAG_MUTABLE
        )
        val notificationChet = NotificationCompat.Builder(
            this,
            CHANNEL_ID_CHET
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("GPS assistant Чётный запущен")
            .setContentIntent(pIntentChet).build()
        startForeground(81, notificationChet)
    }

    private fun initLocationChet(){
        locRequestChet = LocationRequest.create()
        locRequestChet.interval = 1000
        locRequestChet.fastestInterval = 1000
        locRequestChet.priority = PRIORITY_HIGH_ACCURACY
        locProviderChet = LocationServices.getFusedLocationProviderClient(baseContext)

    }

    private fun startLocationUpdatesChet(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) return

        locProviderChet.requestLocationUpdates(
            locRequestChet,
            locCallbackChet,
            Looper.myLooper()
        )
    }

    private suspend fun calculationKm() = withContext(Dispatchers.IO){
        var q = 1001
        var q1 = 1101
        var q2 = 1201
        var q3 = 1301
        var q4 = 1401
        var q5 = 1501
        var q6 = 1601
        var q7 = 1701
        var q8 = 1801
        var q9 = 1901

        var w = 1099
        var w1 = 1199
        var w2 = 1299
        var w3 = 1399
        var w4 = 1499
        var w5 = 1599
        var w6 = 1699
        var w7 = 1799
        var w8 = 1899
        var w9 = 1999

        var qkm = 2
        var wkm = 2

        var qpk = 1
        var wpk = 1

        while (q < 9999999) {
            q += 1000
            q1 += 1000
            q2 += 1000
            q3 += 1000
            q4 += 1000
            q5 += 1000
            q6 += 1000
            q7 += 1000
            q8 += 1000
            q9 += 1000

            w += 1000
            w1 += 1000
            w2 += 1000
            w3 += 1000
            w4 += 1000
            w5 += 1000
            w6 += 1000
            w7 += 1000
            w8 += 1000
            w9 += 1000

            qkm += 1
            wkm += 1

            if (distanceChet >= q && distanceChet <= w){
                qpk = 1
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q
                faktFinishKmChet = w
            }

            if (distanceChet >= q1 && distanceChet <= w1){
                qpk = 2
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q1
                faktFinishKmChet = w1
            }

            if (distanceChet >= q2 && distanceChet <= w2){
                qpk = 3
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q2
                faktFinishKmChet = w2
            }

            if (distanceChet >= q3 && distanceChet <= w3){
                qpk = 4
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q3
                faktFinishKmChet = w3
            }

            if (distanceChet >= q4 && distanceChet <= w4){
                qpk = 5
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q4
                faktFinishKmChet = w4
            }

            if (distanceChet >= q5 && distanceChet <= w5){
                qpk = 6
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q5
                faktFinishKmChet = w5
            }

            if (distanceChet >= q6 && distanceChet <= w6){
                qpk = 7
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q6
                faktFinishKmChet = w6
            }

            if (distanceChet >= q7 && distanceChet <= w7){
                qpk = 8
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q7
                faktFinishKmChet = w7
            }

            if (distanceChet >= q8 && distanceChet <= w8){
                qpk = 9
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q8
                faktFinishKmChet = w8
            }

            if (distanceChet >= q9 && distanceChet <= w9){
                qpk = 10
                kmDistanceChet = qkm
                pkDistanceChet = qpk

                faktStartKmChet = q9
                faktFinishKmChet = w9
            }
        }
    }

    companion object{
        const val LOC_MODEL_INTENT_CHET = "loc_intent_chet"
        const val CHANNEL_ID_CHET = "channel_chet_1"
        var isRunningChet = false
//        var startTimeChet = 0L
    }
}