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
import com.mikhail_R_gps_tracker.gpsassistant.ActivityChet
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.db.MyDbManager
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.MyDbManagerPantograph
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.MainFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.ViewTrackFragment.Companion.FRAGMENT_LAT_LONG_KM_TO_SERVICE_FAKT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Suppress("DEPRECATION")
class LocationServiceChetFakt : Service() {
    val myLimitation = mutableListOf<Int>()
    val myPantograph = mutableListOf<Int>()
    val myBrake = mutableListOf<Int>()

    val myLimitationList = mutableListOf<Int>()
    val myPantographList = mutableListOf<Int>()
    val myBrakeList = mutableListOf<Int>()

    private val distanceKmFakt = ArrayList<ListLatLongKmFakt>()
    private var distanceChet: Float = 0.0f
    private var distanceChetFaktLimitation: Float = 0.0f
    private var distanceChetFaktLimitation2500: Float = 0.0f
    private var distanceChetFaktBrake: Float = 0.0f
    private var distanceChetFaktBrake3500: Float = 0.0f
    private var distanceChetFaktPantograph: Float = 0.0f
    private var distanceChetFaktPantograph4500: Float = 0.0f
    private var ChetBrake: Float = 0.0f
    private var ChetPantograph: Float = 0.0f
    private var isChekLimitation = true
    private var isChekMutableLimitation = false
    private var isChekPantograph = true
    private var isChekBrake = true
    private var isOgr = true


    private var limitation1: Int = 0
    private var brake1: Int = 0
    private var pantograph1: Int = 0




    private var fragmentLatLongToServiceChetFakt: String = ""
    private var isCheckTrackChetFakt = true

    var kmDistanceChetFakt: Int = 0
    var kmDistanceChet2000: Int = 0
    var pkDistanceChetFakt: Int = 0

    private var lastLocationChetFakt: Location? = null
    private lateinit var locProviderChetFakt: FusedLocationProviderClient
    private lateinit var locRequestChetFakt: LocationRequest
    private var isDebagChetFakt = true

    private lateinit var soundPool: SoundPool

    private lateinit var assetManager: AssetManager

    private var voice15: Int = 0
    private var voice25: Int = 0
    private var voice40: Int = 0
    private var voice50: Int = 0
    private var voice55: Int = 0
    private var voice60: Int = 0
    private var voice65: Int = 0
    private var voice70: Int = 0
    private var voice75: Int = 0
    private var voiceprev: Int = 0
    private var voiceprev25: Int = 0
    private var saut: Int = 0
    private var pantograph: Int = 0
    private var brake: Int = 0
    private var songvipolneno: Int = 0

    private var ogr15: Int = 0
    private var ogr25: Int = 0
    private var ogr40: Int = 0
    private var ogr50: Int = 0
    private var ogr55: Int = 0
    private var ogr60: Int = 0
    private var ogr65: Int = 0
    private var ogr70: Int = 0
    private var ogr75: Int = 0

    private var streamID = 0
    private var uslChet = 0.0F
    private var sumCalculateUslChet = 1.0F
    private lateinit var myDbManagerLimitations: MyDbManagerLimitations
    private lateinit var myDbManagerPantograph: MyDbManagerPantograph
    private lateinit var myDbManagerBrake: MyDbManagerBrake

    private lateinit var myDbManager: MyDbManager

    private var isPantographChet = true
    private var isBrakeChet = true

    private var isChekBrakeChet = true
    private var isChekPantographChet = true

    private var isChekPantographChet2 = true
    private var isChekBrakeChet2 = true
    private var isChekPantographChet3 = true

    private var isLimitationsChet15 = true
    private var isLimitationsChet25 = true
    private var isLimitationsChet40 = true
    private var isLimitationsChet50 = true
    private var isLimitationsChet55 = true
    private var isLimitationsChet60 = true
    private var isLimitationsChet65 = true
    private var isLimitationsChet70 = true
    private var isLimitationsChet75 = true

    private var isLimitationsChet400m15 = true
    private var isLimitationsChet400m25 = true
    private var isLimitationsChet400m40 = true
    private var isLimitationsChet400m50 = true
    private var isLimitationsChet400m55 = true
    private var isLimitationsChet400m60 = true
    private var isLimitationsChet400m65 = true
    private var isLimitationsChet400m70 = true
    private var isLimitationsChet400m75 = true

    private var isSautChet15 = true
    private var isSautChet25 = true
    private var isSautChet40 = true
    private var isSautChet50 = true
    private var isSautChet55 = true
    private var isSautChet60 = true
    private var isSautChet65 = true
    private var isSautChet70 = true
    private var isSautChet75 = true
    private var isSautChet2 = true

    private var int15: Int = 150
    private var int25: Int = 150
    private var int40: Int = 150
    private var int50: Int = 150
    private var int55: Int = 150
    private var int60: Int = 150
    private var int65: Int = 150
    private var int70: Int = 150
    private var int75: Int = 150

    private var int15Chet: Int = 150
    private var int25Chet: Int = 150
    private var int40Chet: Int = 150
    private var int50Chet: Int = 150
    private var int55Chet: Int = 150
    private var int60Chet: Int = 150
    private var int65Chet: Int = 150
    private var int70Chet: Int = 150
    private var int75Chet: Int = 150

    var tvOgrChet15: String = ""
    var tvOgrChet25: String = ""
    var tvOgrChet40: String = ""
    var tvOgrChet50: String = ""
    var tvOgrChet55: String = ""
    var tvOgrChet60: String = ""
    var tvOgrChet65: String = ""
    var tvOgrChet70: String = ""
    var tvOgrChet75: String = ""

    var tvKmPkChet15: String = ""
    var tvKmPkChet25: String = ""
    var tvKmPkChet40: String = ""
    var tvKmPkChet50: String = ""
    var tvKmPkChet55: String = ""
    var tvKmPkChet60: String = ""
    var tvKmPkChet65: String = ""
    var tvKmPkChet70: String = ""
    var tvKmPkChet75: String = ""

    private var faktStartKmChet: Int = 0
    private var faktFinishKmChet: Int = 0

    private var titleStartChet: Int = 0
    private var piketStartChet: Int = 0
    private var faktNachKmChet: Int = 0
    private var piketNachKmChet: Int = 0

    private var titleFinishChet: Int = 0
    private var piketFinishChet: Int = 0
    private var speedChet: Int = 0
    private var speedChetMin: Int = 0
    private var speedChetMinChet: Int = 0
    private var faktEndKmChet: Int = 0
    private var piketEndKmChet: Int = 0

    private var titleStartPantographChet: Int = 0
    private var piketStartPantographChet: Int = 0
    private var faktNachKmPantographChet: Int = 0
    private var piketNachKmPantographChet: Int = 0

    private var titleStartBrakeChet: Int = 0
    private var piketStartBrakeChet: Int = 0
    private var faktNachKmBrakeChet: Int = 0
    private var piketNachKmBrakeChet: Int = 0

    private var faktNachKm2000: Int = 0
    private var faktNachKm1901: Int = 0
    private var faktNachKm1900: Int = 0
    private var faktNachKm1801: Int = 0
    private var faktNachKm1800: Int = 0
    private var faktNachKm1701: Int = 0
    private var faktNachKm1700: Int = 0
    private var faktNachKm1601: Int = 0
    private var faktNachKm1600: Int = 0
    private var faktNachKm1501: Int = 0

    private var faktNachKmPantograph3000: Int = 0
    private var faktNachKmPantograph2901: Int = 0
    private var faktNachKmPantograph2900: Int = 0
    private var faktNachKmPantograph2801: Int = 0
    private var faktNachKmPantograph2800: Int = 0
    private var faktNachKmPantograph2701: Int = 0
    private var faktNachKmPantograph2700: Int = 0
    private var faktNachKmPantograph2601: Int = 0
    private var faktNachKmPantograph2600: Int = 0
    private var faktNachKmPantograph2501: Int = 0

    private var faktNachKmPantograph2500: Int = 0
    private var faktNachKmPantograph2401: Int = 0
    private var faktNachKmPantograph2400: Int = 0
    private var faktNachKmPantograph2301: Int = 0
    private var faktNachKmPantograph2300: Int = 0
    private var faktNachKmPantograph2201: Int = 0
    private var faktNachKmPantograph2200: Int = 0
    private var faktNachKmPantograph2101: Int = 0
    private var faktNachKmPantograph2100: Int = 0
    private var faktNachKmPantograph2001: Int = 0

    private var faktNachKmBrake4000: Int = 0
    private var faktNachKmBrake3901: Int = 0
    private var faktNachKmBrake3900: Int = 0
    private var faktNachKmBrake3801: Int = 0
    private var faktNachKmBrake3800: Int = 0
    private var faktNachKmBrake3701: Int = 0
    private var faktNachKmBrake3700: Int = 0
    private var faktNachKmBrake3601: Int = 0
    private var faktNachKmBrake3600: Int = 0
    private var faktNachKmBrake3501: Int = 0

    private var faktNachKmBrake2900: Int = 0
    private var faktNachKmBrake2801: Int = 0
    private var faktNachKmBrake2800: Int = 0
    private var faktNachKmBrake2701: Int = 0
    private var faktNachKmBrake2700: Int = 0
    private var faktNachKmBrake2601: Int = 0
    private var faktNachKmBrake2600: Int = 0
    private var faktNachKmBrake2501: Int = 0
    private var faktNachKmBrake2500: Int = 0
    private var faktNachKmBrake2401: Int = 0

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private val receiverUslChet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentUslChet: Intent?) {
            if (intentUslChet?.action == MainFragmentChet.LOC_MODEL_INTENT_FRAGMENT_USL_CHET){
                val fragmentModelUslChet = intentUslChet.getSerializableExtra(MainFragmentChet.LOC_MODEL_INTENT_FRAGMENT_USL_CHET) as FragmentModelUslChet
                uslChet = fragmentModelUslChet.mainUslChet
                Log.d("MyLog", "uslChet: $uslChet")


            }
        }
    }

    private fun registerLocReceiverUslChet(){
        val locFilterUslChet = IntentFilter(MainFragmentChet.LOC_MODEL_INTENT_FRAGMENT_USL_CHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverUslChet, locFilterUslChet)
    }

    private val receiverLatLongKmToServiceFakt = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentLatLongKmToServiceFakt: Intent?) {
            if (intentLatLongKmToServiceFakt?.action == FRAGMENT_LAT_LONG_KM_TO_SERVICE_FAKT){
                val fragmentLatLongKmToServiceChetFakt = intentLatLongKmToServiceFakt.getSerializableExtra(
                    FRAGMENT_LAT_LONG_KM_TO_SERVICE_FAKT) as FragmentLatLongKmToServiceChet
                if (isCheckTrackChetFakt && fragmentLatLongToServiceChetFakt == "") {
                    fragmentLatLongToServiceChetFakt = fragmentLatLongKmToServiceChetFakt.fragmentLatLongKmToServiceChet
                    Log.d("MyLog", "FragmentLatLongKmToServiceChetFakt: $fragmentLatLongToServiceChetFakt")
                    isCheckTrackChetFakt = !isCheckTrackChetFakt
                }
            }
        }
    }
    private fun registerLatLongKmToServiceFakt(){
        val locFilterLatLongKmToServiceFakt = IntentFilter(FRAGMENT_LAT_LONG_KM_TO_SERVICE_FAKT)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverLatLongKmToServiceFakt, locFilterLatLongKmToServiceFakt)
    }

    private fun getLatLongKmChetFakt() {
        if (distanceKmFakt.indices.isEmpty()) {
            val list = fragmentLatLongToServiceChetFakt.split("/")
            list.forEach {
                if (it.isEmpty()) return@forEach
                val points = it.split(",")
                distanceKmFakt.add(ListLatLongKmFakt(points[0].toFloat(), points[1].toFloat(), points[2].toFloat()))
            }
        }
    }

    data class ListLatLongKmFakt(val lat: Float,
                                 val long: Float,
                                 val distKm: Float){
        override fun toString(): String {
            return super.toString()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        myDbManagerPantograph = MyDbManagerPantograph(applicationContext)
        myDbManagerBrake = MyDbManagerBrake(applicationContext)
        myDbManagerLimitations = MyDbManagerLimitations(applicationContext)
        registerLocReceiverUslChet()
        registerLatLongKmToServiceFakt()
        startNotificationChetFakt()
        startLocationUpdatesChetFakt()
        isRunningChetFakt = true
        return START_STICKY
    }

    private fun playSound(sound: Int): Int {
        if (sound > 0) {
            streamID = soundPool.play(sound, 1F, 1F, 1, 0, 1F)
        }
        return streamID
    }

    private fun loadSound(fileName: String): Int {
        val afd: AssetFileDescriptor = try {
            application.assets.openFd(fileName)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("MyLog", "Не могу загрузить файл $fileName")

            return -1
        }
//        return soundPool.load(afd.fileDescriptor, 1000, 1000, 1)
        return soundPool.load(afd,1)
    }

    override fun onCreate() {
        super.onCreate()
        initLocationChetFakt()

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()

        assetManager = assets
        voice15 = loadSound("voice15.mp3")
        voice25 = loadSound("voice25.mp3")
        voice40 = loadSound("voice40.mp3")
        voice50 = loadSound("voice50.mp3")
        voice55 = loadSound("voice55.mp3")
        voice60 = loadSound("voice60.mp3")
        voice65 = loadSound("voice65.mp3")
        voice70 = loadSound("voice70.mp3")
        voice75 = loadSound("voice75.mp3")
        voiceprev = loadSound("voiceprev.mp3")
        voiceprev25 = loadSound("voiceprev25.mp3")
        saut = loadSound("saut.mp3")
        pantograph = loadSound("pantograph.mp3")
        brake = loadSound("brake.mp3")
        songvipolneno = loadSound("songvipolneno.mp3")

        ogr15 = loadSound("ogr15.mp3")
        ogr25 = loadSound("ogr25.mp3")
        ogr40 = loadSound("ogr40.mp3")
        ogr50 = loadSound("ogr50.mp3")
        ogr55 = loadSound("ogr55.mp3")
        ogr60 = loadSound("ogr60.mp3")
        ogr65 = loadSound("ogr65.mp3")
        ogr70 = loadSound("ogr70.mp3")
        ogr75 = loadSound("ogr75.mp3")
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunningChetFakt = false
        locProviderChetFakt.removeLocationUpdates(locCallbackChetFakt)
    }

    private val locCallbackChetFakt = object : LocationCallback(){
        override fun onLocationResult(lResultChetFakt: LocationResult) {
            super.onLocationResult(lResultChetFakt)
            val currentLocationChet = lResultChetFakt.lastLocation
            if (lastLocationChetFakt != null && currentLocationChet != null){
                if (currentLocationChet.speed > 0.4 || isDebagChetFakt){

                    getLatLongKmChetFakt()

                    val mLoc = Location("").apply {
                        latitude = currentLocationChet.latitude
                        longitude = currentLocationChet.longitude
                    }
                    var minDistance = 10000f
                    var distanceIndex = 0

                    for (i in distanceKmFakt.indices){
                        val pointLoc = Location("").apply {
                            latitude = distanceKmFakt[i].lat.toDouble()
                            longitude = distanceKmFakt[i].long.toDouble()
                        }
                        val dist = mLoc.distanceTo(pointLoc)
                        if (dist < 2000){
                            if (minDistance > dist){
                                minDistance = dist
                                distanceIndex = i
                            }
                        }
                    }

                    if (minDistance < 2000){
                        distanceChet = distanceKmFakt[distanceIndex].distKm
                        distanceChetFaktLimitation2500 = distanceKmFakt[distanceIndex].distKm + 2500
                        distanceChetFaktLimitation = distanceKmFakt[distanceIndex].distKm + 2000
                        distanceChetFaktBrake3500 = distanceKmFakt[distanceIndex].distKm + 3500
                        distanceChetFaktBrake = distanceKmFakt[distanceIndex].distKm + 3000
                        distanceChetFaktPantograph4500 = distanceKmFakt[distanceIndex].distKm + 4500
                        distanceChetFaktPantograph = distanceKmFakt[distanceIndex].distKm + 4000
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        calculationKmFakt()
                    }

                    val myLimitation1 = myLimitation.toMutableSet().sorted()
                    val myBrake1 = myBrake.toMutableSet().sorted()
                    val myPantograph1 = myPantograph.toMutableSet().sorted()

                    Log.d("MyLog", "myLimitationList => $myLimitationList")
                    Log.d("MyLog", "myBrakeList => $myBrakeList")
                    Log.d("MyLog", "myPantographList => $myPantographList")

                    if (myLimitation.isNotEmpty()){
                        for (itemLimitation in myLimitation1){
                            if (faktStartKmChet != 0){
                                if (faktStartKmChet + 2000 == itemLimitation){
                                    if (myLimitationList.isEmpty() && limitation1 != itemLimitation){
                                        myLimitationList.add(itemLimitation)
                                        limitation1 = itemLimitation
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d("MyLog", "Список 'myLimitation' пуст!")
                    }

                    if (myBrake.isNotEmpty()){
                        for (itemBrake in myBrake1){
                            if (faktStartKmChet != 0){
                                if (faktStartKmChet + 4000 == itemBrake){
                                    if (myBrakeList.isEmpty() && brake1 != itemBrake){
                                        myBrakeList.add(itemBrake)
                                        brake1 = itemBrake
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d("MyLog", "Список 'myBrake' пуст!")
                    }

                    if (myPantograph.isNotEmpty()){
                        for (itemPantograph in myPantograph1){
                            if (faktStartKmChet != 0){
                                if (faktStartKmChet + 3000 == itemPantograph){
                                    if (myPantographList.isEmpty() && pantograph1 != itemPantograph){
                                        myPantographList.add(itemPantograph)
                                        pantograph1 = itemPantograph
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d("MyLog", "Список 'myPantograph' пуст!")
                    }

                    if (myLimitationList.isNotEmpty()){
                        if (myBrakeList.isNotEmpty()){
                            if (myPantographList.isNotEmpty()){
                                for (itemLimitationsList in myLimitationList)
                                    for (itemBrakesList in myBrakeList){
                                        for (itemPantographsList in myPantographList){
                                            if (faktStartKmChet + 2000 == itemLimitationsList && faktStartKmChet + 4000 == itemBrakesList && faktStartKmChet + 3000 == itemPantographsList){
                                                Log.d("MyLog", "Озвучиваем Ограничение скорости! 1")

                                                if (speedChetMin == 15 && isLimitationsChet15){
                                                    playSound(voice15)
                                                    isLimitationsChet15 = !isLimitationsChet15
                                                    isSautChet15 = !isSautChet15
                                                }
                                                if (speedChetMin == 25 && isLimitationsChet25){
                                                    playSound(voice25)
                                                    isLimitationsChet25 = !isLimitationsChet25
                                                    isSautChet25 = !isSautChet25
                                                }
                                                if (speedChetMin == 40 && isLimitationsChet40){
                                                    playSound(voice40)
                                                    isLimitationsChet40 = !isLimitationsChet40
                                                    isSautChet40 = !isSautChet40
                                                }
                                                if (speedChetMin == 50 && isLimitationsChet50){
                                                    playSound(voice50)
                                                    isLimitationsChet50 = !isLimitationsChet50
                                                    isSautChet50 = !isSautChet50
                                                }
                                                if (speedChetMin == 55 && isLimitationsChet55){
                                                    playSound(voice55)
                                                    isLimitationsChet55 = !isLimitationsChet55
                                                    isSautChet55 = !isSautChet55
                                                }
                                                if (speedChetMin == 60 && isLimitationsChet60){
                                                    playSound(voice60)
                                                    isLimitationsChet60 = !isLimitationsChet60
                                                    isSautChet60 = !isSautChet60
                                                }
                                                if (speedChetMin == 65 && isLimitationsChet65){
                                                    playSound(voice65)
                                                    isLimitationsChet65 = !isLimitationsChet65
                                                    isSautChet65 = !isSautChet65
                                                }
                                                if (speedChetMin == 70 && isLimitationsChet70){
                                                    playSound(voice70)
                                                    isLimitationsChet70 = !isLimitationsChet70
                                                    isSautChet70 = !isSautChet70
                                                }
                                                if (speedChetMin == 75 && isLimitationsChet75){
                                                    playSound(voice75)
                                                    isLimitationsChet75 = !isLimitationsChet75
                                                    isSautChet75 = !isSautChet75
                                                }

                                                myLimitationList.removeAt(0)
                                                myBrakeList[0] = itemBrakesList + 200
                                                myPantographList[0] = itemPantographsList + 100
                                            }
                                            if (faktStartKmChet + 2000 == itemLimitationsList && faktStartKmChet + 4100 == itemBrakesList && faktStartKmChet + 3000 == itemPantographsList){
                                                Log.d("MyLog", "Озвучиваем Ограничение скорости! 2")

                                                if (speedChetMin == 15 && isLimitationsChet15){
                                                    playSound(voice15)
                                                    isLimitationsChet15 = !isLimitationsChet15
                                                    isSautChet15 = !isSautChet15
                                                }
                                                if (speedChetMin == 25 && isLimitationsChet25){
                                                    playSound(voice25)
                                                    isLimitationsChet25 = !isLimitationsChet25
                                                    isSautChet25 = !isSautChet25
                                                }
                                                if (speedChetMin == 40 && isLimitationsChet40){
                                                    playSound(voice40)
                                                    isLimitationsChet40 = !isLimitationsChet40
                                                    isSautChet40 = !isSautChet40
                                                }
                                                if (speedChetMin == 50 && isLimitationsChet50){
                                                    playSound(voice50)
                                                    isLimitationsChet50 = !isLimitationsChet50
                                                    isSautChet50 = !isSautChet50
                                                }
                                                if (speedChetMin == 55 && isLimitationsChet55){
                                                    playSound(voice55)
                                                    isLimitationsChet55 = !isLimitationsChet55
                                                    isSautChet55 = !isSautChet55
                                                }
                                                if (speedChetMin == 60 && isLimitationsChet60){
                                                    playSound(voice60)
                                                    isLimitationsChet60 = !isLimitationsChet60
                                                    isSautChet60 = !isSautChet60
                                                }
                                                if (speedChetMin == 65 && isLimitationsChet65){
                                                    playSound(voice65)
                                                    isLimitationsChet65 = !isLimitationsChet65
                                                    isSautChet65 = !isSautChet65
                                                }
                                                if (speedChetMin == 70 && isLimitationsChet70){
                                                    playSound(voice70)
                                                    isLimitationsChet70 = !isLimitationsChet70
                                                    isSautChet70 = !isSautChet70
                                                }
                                                if (speedChetMin == 75 && isLimitationsChet75){
                                                    playSound(voice75)
                                                    isLimitationsChet75 = !isLimitationsChet75
                                                    isSautChet75 = !isSautChet75
                                                }

                                                myLimitationList.removeAt(0)
                                                myBrakeList[0] = itemBrakesList + 100
                                                myPantographList[0] = itemPantographsList + 100
                                            }
                                            if (faktStartKmChet + 2000 == itemLimitationsList && faktStartKmChet + 4000 == itemBrakesList && faktStartKmChet + 2900 == itemPantographsList){
                                                Log.d("MyLog", "Озвучиваем Ограничение скорости! 3")

                                                if (speedChetMin == 15 && isLimitationsChet15){
                                                    playSound(voice15)
                                                    isLimitationsChet15 = !isLimitationsChet15
                                                    isSautChet15 = !isSautChet15
                                                }
                                                if (speedChetMin == 25 && isLimitationsChet25){
                                                    playSound(voice25)
                                                    isLimitationsChet25 = !isLimitationsChet25
                                                    isSautChet25 = !isSautChet25
                                                }
                                                if (speedChetMin == 40 && isLimitationsChet40){
                                                    playSound(voice40)
                                                    isLimitationsChet40 = !isLimitationsChet40
                                                    isSautChet40 = !isSautChet40
                                                }
                                                if (speedChetMin == 50 && isLimitationsChet50){
                                                    playSound(voice50)
                                                    isLimitationsChet50 = !isLimitationsChet50
                                                    isSautChet50 = !isSautChet50
                                                }
                                                if (speedChetMin == 55 && isLimitationsChet55){
                                                    playSound(voice55)
                                                    isLimitationsChet55 = !isLimitationsChet55
                                                    isSautChet55 = !isSautChet55
                                                }
                                                if (speedChetMin == 60 && isLimitationsChet60){
                                                    playSound(voice60)
                                                    isLimitationsChet60 = !isLimitationsChet60
                                                    isSautChet60 = !isSautChet60
                                                }
                                                if (speedChetMin == 65 && isLimitationsChet65){
                                                    playSound(voice65)
                                                    isLimitationsChet65 = !isLimitationsChet65
                                                    isSautChet65 = !isSautChet65
                                                }
                                                if (speedChetMin == 70 && isLimitationsChet70){
                                                    playSound(voice70)
                                                    isLimitationsChet70 = !isLimitationsChet70
                                                    isSautChet70 = !isSautChet70
                                                }
                                                if (speedChetMin == 75 && isLimitationsChet75){
                                                    playSound(voice75)
                                                    isLimitationsChet75 = !isLimitationsChet75
                                                    isSautChet75 = !isSautChet75
                                                }

                                                myLimitationList.removeAt(0)
                                                myBrakeList[0] = itemBrakesList + 100
                                                myPantographList[0] = itemPantographsList + 100
                                            }
                                        }
                                    }
                            }
                        }
                    }

                    Log.d("MyLog", "speedChetMin => => => $speedChetMin")

                    if (myPantographList.isEmpty()){
                        if (myBrakeList.isEmpty()){
                            if (myLimitationList.isNotEmpty()){
                                for (itemLimitationsList in myLimitationList){
                                    if (faktStartKmChet + 2000 == itemLimitationsList){

                                        Log.d("MyLog", "Озвучиваем Ограничение скорости! 4")
                                        Log.d("MyLog", "speedChetMin => => => $speedChetMin")

                                        if (speedChetMin == 15 && isLimitationsChet15){
                                            playSound(voice15)
                                            isLimitationsChet15 = !isLimitationsChet15
                                            isSautChet15 = !isSautChet15
                                        }
                                        if (speedChetMin == 25 && isLimitationsChet25){
                                            playSound(voice25)
                                            isLimitationsChet25 = !isLimitationsChet25
                                            isSautChet25 = !isSautChet25
                                        }
                                        if (speedChetMin == 40 && isLimitationsChet40){
                                            playSound(voice40)
                                            isLimitationsChet40 = !isLimitationsChet40
                                            isSautChet40 = !isSautChet40
                                        }
                                        if (speedChetMin == 50 && isLimitationsChet50){
                                            playSound(voice50)
                                            isLimitationsChet50 = !isLimitationsChet50
                                            isSautChet50 = !isSautChet50
                                        }
                                        if (speedChetMin == 55 && isLimitationsChet55){
                                            playSound(voice55)
                                            isLimitationsChet55 = !isLimitationsChet55
                                            isSautChet55 = !isSautChet55
                                        }
                                        if (speedChetMin == 60 && isLimitationsChet60){
                                            playSound(voice60)
                                            isLimitationsChet60 = !isLimitationsChet60
                                            isSautChet60 = !isSautChet60
                                        }
                                        if (speedChetMin == 65 && isLimitationsChet65){
                                            playSound(voice65)
                                            isLimitationsChet65 = !isLimitationsChet65
                                            isSautChet65 = !isSautChet65
                                        }
                                        if (speedChetMin == 70 && isLimitationsChet70){
                                            playSound(voice70)
                                            isLimitationsChet70 = !isLimitationsChet70
                                            isSautChet70 = !isSautChet70
                                        }
                                        if (speedChetMin == 75 && isLimitationsChet75){
                                            playSound(voice75)
                                            isLimitationsChet75 = !isLimitationsChet75
                                            isSautChet75 = !isSautChet75
                                        }

                                        myLimitationList.removeAt(0)
                                    }
                                }
                            }
                        }
                    }

                    if (myLimitationList.isNotEmpty()){
                        if (myBrakeList.isNotEmpty()){
                            if (myPantographList.isEmpty()){
                                for (itemLimitationsList in myLimitationList)
                                    for (itemBrakesList in myBrakeList){
                                        if (faktStartKmChet + 2000 == itemLimitationsList && faktStartKmChet + 4000 == itemBrakesList){
                                            Log.d("MyLog", "Озвучиваем Ограничение скорости! 5")

                                            if (speedChetMin == 15 && isLimitationsChet15){
                                                playSound(voice15)
                                                isLimitationsChet15 = !isLimitationsChet15
                                                isSautChet15 = !isSautChet15
                                            }
                                            if (speedChetMin == 25 && isLimitationsChet25){
                                                playSound(voice25)
                                                isLimitationsChet25 = !isLimitationsChet25
                                                isSautChet25 = !isSautChet25
                                            }
                                            if (speedChetMin == 40 && isLimitationsChet40){
                                                playSound(voice40)
                                                isLimitationsChet40 = !isLimitationsChet40
                                                isSautChet40 = !isSautChet40
                                            }
                                            if (speedChetMin == 50 && isLimitationsChet50){
                                                playSound(voice50)
                                                isLimitationsChet50 = !isLimitationsChet50
                                                isSautChet50 = !isSautChet50
                                            }
                                            if (speedChetMin == 55 && isLimitationsChet55){
                                                playSound(voice55)
                                                isLimitationsChet55 = !isLimitationsChet55
                                                isSautChet55 = !isSautChet55
                                            }
                                            if (speedChetMin == 60 && isLimitationsChet60){
                                                playSound(voice60)
                                                isLimitationsChet60 = !isLimitationsChet60
                                                isSautChet60 = !isSautChet60
                                            }
                                            if (speedChetMin == 65 && isLimitationsChet65){
                                                playSound(voice65)
                                                isLimitationsChet65 = !isLimitationsChet65
                                                isSautChet65 = !isSautChet65
                                            }
                                            if (speedChetMin == 70 && isLimitationsChet70){
                                                playSound(voice70)
                                                isLimitationsChet70 = !isLimitationsChet70
                                                isSautChet70 = !isSautChet70
                                            }
                                            if (speedChetMin == 75 && isLimitationsChet75){
                                                playSound(voice75)
                                                isLimitationsChet75 = !isLimitationsChet75
                                                isSautChet75 = !isSautChet75
                                            }

                                            myLimitationList.removeAt(0)
                                            myBrakeList[0] = itemBrakesList + 100
                                        }
                                    }
                            }
                        }
                    }

                    if (myLimitationList.isNotEmpty()){
                        if (myPantographList.isNotEmpty()){
                            if (myBrakeList.isEmpty()){
                                for (itemLimitationsList in myLimitationList)
                                    for (itemPantographsList in myPantographList){
                                        if (faktStartKmChet + 2000 == itemLimitationsList && faktStartKmChet + 3000 == itemPantographsList){
                                            Log.d("MyLog", "Озвучиваем Ограничение скорости! 6")

                                            if (speedChetMin == 15 && isLimitationsChet15){
                                                playSound(voice15)
                                                isLimitationsChet15 = !isLimitationsChet15
                                                isSautChet15 = !isSautChet15
                                            }
                                            if (speedChetMin == 25 && isLimitationsChet25){
                                                playSound(voice25)
                                                isLimitationsChet25 = !isLimitationsChet25
                                                isSautChet25 = !isSautChet25
                                            }
                                            if (speedChetMin == 40 && isLimitationsChet40){
                                                playSound(voice40)
                                                isLimitationsChet40 = !isLimitationsChet40
                                                isSautChet40 = !isSautChet40
                                            }
                                            if (speedChetMin == 50 && isLimitationsChet50){
                                                playSound(voice50)
                                                isLimitationsChet50 = !isLimitationsChet50
                                                isSautChet50 = !isSautChet50
                                            }
                                            if (speedChetMin == 55 && isLimitationsChet55){
                                                playSound(voice55)
                                                isLimitationsChet55 = !isLimitationsChet55
                                                isSautChet55 = !isSautChet55
                                            }
                                            if (speedChetMin == 60 && isLimitationsChet60){
                                                playSound(voice60)
                                                isLimitationsChet60 = !isLimitationsChet60
                                                isSautChet60 = !isSautChet60
                                            }
                                            if (speedChetMin == 65 && isLimitationsChet65){
                                                playSound(voice65)
                                                isLimitationsChet65 = !isLimitationsChet65
                                                isSautChet65 = !isSautChet65
                                            }
                                            if (speedChetMin == 70 && isLimitationsChet70){
                                                playSound(voice70)
                                                isLimitationsChet70 = !isLimitationsChet70
                                                isSautChet70 = !isSautChet70
                                            }
                                            if (speedChetMin == 75 && isLimitationsChet75){
                                                playSound(voice75)
                                                isLimitationsChet75 = !isLimitationsChet75
                                                isSautChet75 = !isSautChet75
                                            }

                                            myLimitationList.removeAt(0)
                                            myPantographList[0] = itemPantographsList + 100
                                        }
                                    }
                            }
                        }
                    }

                    if (myLimitationList.isEmpty()){
                        if (myBrakeList.isNotEmpty()){
                            if (myPantographList.isNotEmpty()){
                                for (itemBrakesList in myBrakeList)
                                    for (itemPantographsList in myPantographList){
                                        if (faktStartKmChet + 3000 == itemPantographsList && faktStartKmChet + 4000 == itemBrakesList){
                                            Log.d("MyLog", "Совпадение Опускания с Торможением!")
                                            myBrakeList[0] = itemBrakesList + 200
                                            myPantographList[0] = itemPantographsList + 100
                                        }
                                    }
                            }
                        }
                    }

                    if (myLimitationList.isEmpty()){
                        if (myPantographList.isNotEmpty()){
                            for (itemPantographsList in myPantographList){
                                if (faktStartKmChet + 3000 == itemPantographsList){
                                    Log.d("MyLog", "Озвучиваем Опускание!")

                                    playSound(pantograph)

                                    myPantographList.removeAt(0)
                                }
                            }
                        }
                    }

                    if (myLimitationList.isEmpty()){
                        if (myPantographList.isEmpty()){
                            if (myBrakeList.isNotEmpty()){
                                for (itemBrakesList in myBrakeList){
                                    if (faktStartKmChet + 4000 == itemBrakesList){
                                        Log.d("MyLog", "Озвучиваем Торможение!")

                                        playSound(brake)

                                        myBrakeList.removeAt(0)
                                    }
                                }
                            }
                        }
                    }

                    //***********************************************************************

//                    if (distanceChet > faktNachKm2000 && distanceChet < faktNachKm1901){
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000){
//                            Log.d("MyLog", "Ни чему не равен!")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//                        }
//                        if (faktStartKmChet == faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000 && isChekBrakeChet2 && isChekPantographChet2){
//                            Log.d("MyLog", "Все равны!")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//
//                            isChekBrakeChet2 = !isChekBrakeChet2
//                            isChekPantographChet2 = !isChekPantographChet2
//                            Log.d("MyLog", "isChekBrakeChet2 = $isChekBrakeChet2")
//                            Log.d("MyLog", "isChekPantographChet2 = $isChekPantographChet2")
//                        }
//
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000 && isChekPantographChet){
//                            Log.d("MyLog", "faktNachKmPantograph3000 равен faktNachKm2000!8")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//
//                            isChekPantographChet = !isChekPantographChet
//                        }
//
//                        if (faktStartKmChet == faktNachKmBrake4000 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000 && isChekBrakeChet){
//                            Log.d("MyLog", "faktNachKmBrake4000 равен faktNachKm2000!6")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//
//                            isChekBrakeChet = !isChekBrakeChet
//                        }
//                    }
//
//                    if (distanceChet > faktNachKm2000 && distanceChet < faktNachKm1901){
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000){
//                            Log.d("MyLog", "Ни чему не равен!")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//                        }
//                        if (faktStartKmChet == faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000 && !isChekBrakeChet2 && !isChekPantographChet2){
//                            Log.d("MyLog", "Все равны!")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//                            Log.d("MyLog", "isChekBrakeChet2 = $isChekBrakeChet2")
//                            Log.d("MyLog", "isChekPantographChet2 = $isChekPantographChet2")
//                        }
//
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000 && !isChekPantographChet){
//                            Log.d("MyLog", "faktNachKmPantograph3000 равен faktNachKm2000!8")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//                            Log.d("MyLog", "isChekPantographChet = $isChekPantographChet")
//                        }
//
//                        if (faktStartKmChet == faktNachKmBrake4000 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet == faktNachKm2000 && !isChekBrakeChet){
//                            Log.d("MyLog", "faktNachKmBrake4000 равен faktNachKm2000!6")
//                            if (speedChetMin == 15 && isLimitationsChet15){
//                                playSound(voice15)
//                                isLimitationsChet15 = !isLimitationsChet15
//                                isSautChet15 = !isSautChet15
//                            }
//                            if (speedChetMin == 25 && isLimitationsChet25){
//                                playSound(voice25)
//                                isLimitationsChet25 = !isLimitationsChet25
//                                isSautChet25 = !isSautChet25
//                            }
//                            if (speedChetMin == 40 && isLimitationsChet40){
//                                playSound(voice40)
//                                isLimitationsChet40 = !isLimitationsChet40
//                                isSautChet40 = !isSautChet40
//                            }
//                            if (speedChetMin == 50 && isLimitationsChet50){
//                                playSound(voice50)
//                                isLimitationsChet50 = !isLimitationsChet50
//                                isSautChet50 = !isSautChet50
//                            }
//                            if (speedChetMin == 55 && isLimitationsChet55){
//                                playSound(voice55)
//                                isLimitationsChet55 = !isLimitationsChet55
//                                isSautChet55 = !isSautChet55
//                            }
//                            if (speedChetMin == 60 && isLimitationsChet60){
//                                playSound(voice60)
//                                isLimitationsChet60 = !isLimitationsChet60
//                                isSautChet60 = !isSautChet60
//                            }
//                            if (speedChetMin == 65 && isLimitationsChet65){
//                                playSound(voice65)
//                                isLimitationsChet65 = !isLimitationsChet65
//                                isSautChet65 = !isSautChet65
//                            }
//                            if (speedChetMin == 70 && isLimitationsChet70){
//                                playSound(voice70)
//                                isLimitationsChet70 = !isLimitationsChet70
//                                isSautChet70 = !isSautChet70
//                            }
//                            if (speedChetMin == 75 && isLimitationsChet75){
//                                playSound(voice75)
//                                isLimitationsChet75 = !isLimitationsChet75
//                                isSautChet75 = !isSautChet75
//                            }
//                            Log.d("MyLog", "isChekBrakeChet = $isChekBrakeChet")
//                        }
//                    }
//
//                    if (!isChekPantographChet){
//                        Log.d("MyLog", "Зашли")
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph2900 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet = true
//                            Log.d("MyLog", "isChekPantographChet900 = $isChekPantographChet")
//                        }
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph2800 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet = true
//                            Log.d("MyLog", "isChekPantographChet800 = $isChekPantographChet")
//                        }
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph2700 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet = true
//                            Log.d("MyLog", "isChekPantographChet700 = $isChekPantographChet")
//                        }
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph2600 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet = true
//                            Log.d("MyLog", "isChekPantographChet600 = $isChekPantographChet")
//                        }
//                    }
//
//                    if (!isChekBrakeChet){
//                        if (faktStartKmChet == faktNachKmBrake3900 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(brake)
//                            isChekBrakeChet = true
//                            Log.d("MyLog", "isChekBrakeChet900 = $isChekBrakeChet")
//                        }
//                        if (faktStartKmChet == faktNachKmBrake3800 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(brake)
//                            isChekBrakeChet = true
//                            Log.d("MyLog", "isChekBrakeChet800 = $isChekBrakeChet")
//                        }
//                        if (faktStartKmChet == faktNachKmBrake3700 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(brake)
//                            isChekBrakeChet = true
//                            Log.d("MyLog", "isChekBrakeChet700 = $isChekBrakeChet")
//                        }
//                        if (faktStartKmChet == faktNachKmBrake3600 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(brake)
//                            isChekBrakeChet = true
//                            Log.d("MyLog", "isChekBrakeChet600 = $isChekBrakeChet")
//                        }
//                    }
//
//                    if (!isChekPantographChet2){
//                        if (faktStartKmChet == faktNachKmPantograph2500 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet2 = true
//                            Log.d("MyLog", "isChekPantographChet2500 = $isChekPantographChet2")
//                        }
//                        if (faktStartKmChet == faktNachKmPantograph2400 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet2 = true
//                            Log.d("MyLog", "isChekPantographChet2400 = $isChekPantographChet2")
//                        }
//                        if (faktStartKmChet == faktNachKmPantograph2300 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet2 = true
//                            Log.d("MyLog", "isChekPantographChet2300 = $isChekPantographChet2")
//                        }
//                        if (faktStartKmChet == faktNachKmPantograph2200 && faktStartKmChet != faktNachKm2000){
//
//                            playSound(pantograph)
//                            isChekPantographChet2 = true
//                            Log.d("MyLog", "isChekPantographChet2200 = $isChekPantographChet2")
//                        }
//                    }
//
//                    if (!isChekBrakeChet2){
//                        if (isChekPantographChet2){
//                            if (faktStartKmChet == faktNachKmBrake2900 && faktStartKmChet != faktNachKm2000){
//
//                                playSound(brake)
//                                isChekBrakeChet2 = true
//                                Log.d("MyLog", "faktNachKmBrake2900 = $isChekBrakeChet2")
//                            }
//                            if (faktStartKmChet == faktNachKmBrake2800 && faktStartKmChet != faktNachKm2000){
//
//                                playSound(brake)
//                                isChekBrakeChet2 = true
//                                Log.d("MyLog", "faktNachKmBrake2800 = $isChekBrakeChet2")
//                            }
//                            if (faktStartKmChet == faktNachKmBrake2700 && faktStartKmChet != faktNachKm2000){
//
//                                playSound(brake)
//                                isChekBrakeChet2 = true
//                                Log.d("MyLog", "faktNachKmBrake2700 = $isChekBrakeChet2")
//                            }
//                            if (faktStartKmChet == faktNachKmBrake2600 && faktStartKmChet != faktNachKm2000){
//
//                                playSound(brake)
//                                isChekBrakeChet2 = true
//                                Log.d("MyLog", "faktNachKmBrake2600 = $isChekBrakeChet2")
//                            }
//                            if (faktStartKmChet == faktNachKmBrake2500 && faktStartKmChet != faktNachKm2000){
//
//                                playSound(brake)
//                                isChekBrakeChet2 = true
//                                Log.d("MyLog", "faktNachKmBrake2500 = $isChekBrakeChet2")
//                            }
//                        }
//                    }
//
//                    if (distanceChet > faktNachKmPantograph3000 && distanceChet < faktNachKmPantograph2901){
//                        if (faktStartKmChet != faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000 && isPantographChet){
//                            Log.d("MyLog", "faktStartKmChet = $faktStartKmChet")
//                            Log.d("MyLog", "faktNachKmBrake4000 = $faktNachKmBrake4000")
//                            Log.d("MyLog", "faktNachKmPantograph3000 = $faktNachKmPantograph3000")
//                            Log.d("MyLog", "faktNachKm2000 = $faktNachKm2000")
//                            playSound(pantograph)
//                            isPantographChet = !isPantographChet
//                            Log.d("MyLog", "isPantographChet = $isPantographChet")
//                        }
//                        if (faktStartKmChet == faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000 && isChekPantographChet3){
//                            playSound(pantograph)
//                            isChekPantographChet3 = !isChekPantographChet3
//                            Log.d("MyLog", "isChekPantographChet3 = $isChekPantographChet3")
//                        }
//                    }
//                    if (distanceChet > faktNachKmPantograph2901 && distanceChet < faktNachKmPantograph2801 && !isPantographChet){
//                        isPantographChet = true
//                        Log.d("MyLog", "isPantographChet = $isPantographChet")
//                    }
//                    if (distanceChet > faktNachKmPantograph2901 && distanceChet < faktNachKmPantograph2801 && !isChekPantographChet3){
//                        isChekPantographChet3 = true
//                        Log.d("MyLog", "isChekPantographChet3 = $isChekPantographChet3")
//                    }
//
//                    if (distanceChet > faktNachKmBrake4000 && distanceChet < faktNachKmBrake3901){
//                        if (faktStartKmChet == faktNachKmBrake4000 && faktStartKmChet != faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000 && isBrakeChet){
//                            Log.d("MyLog", "faktStartKmChet = $faktStartKmChet")
//                            Log.d("MyLog", "faktNachKmBrake4000 = $faktNachKmBrake4000")
//                            Log.d("MyLog", "faktNachKmPantograph3000 = $faktNachKmPantograph3000")
//                            Log.d("MyLog", "faktNachKm2000 = $faktNachKm2000")
//                            playSound(brake)
//                            isBrakeChet = !isBrakeChet
//                            Log.d("MyLog", "isBrakeChet = $isBrakeChet")
//                        }
//                        if (faktStartKmChet == faktNachKmBrake4000 && faktStartKmChet == faktNachKmPantograph3000 && faktStartKmChet != faktNachKm2000 && isChekBrakeChet){
//                            isChekBrakeChet = !isChekBrakeChet
//                            Log.d("MyLog", "isChekBrakeChet = $isChekBrakeChet")
//                        }
//                    }
//                    if (distanceChet > faktNachKmBrake3901 && distanceChet < faktNachKmBrake3801 && !isBrakeChet){
//                        isBrakeChet = true
//                        Log.d("MyLog", "isBrakeChet = $isBrakeChet")
//                    }

                    // Конец оповещения

                    CoroutineScope(Dispatchers.Main).launch {
                        myPantograph.clear()
                        myDbManagerPantograph.openDb()
                        val dataListPantographs = myDbManagerPantograph.readDbDataPantographChet()
                        for (item in dataListPantographs){

                            titleStartPantographChet = item.startChet
                            piketStartPantographChet = item.picketStartChet

                            // Начало расчёта начала Опускания токоприемников по киллометро

                            var z = 1001
                            var kms = 2
                            val pkc = 1

                            while (z < 9999999){
                                z += 1000
                                kms += 1

                                if (titleStartPantographChet == kms && piketStartPantographChet == pkc){
                                    faktNachKmPantographChet = z
                                    piketNachKmPantographChet = pkc
                                }
                            }

                            var z1 = 1101
                            var kms1 = 2
                            val pkc1 = 2

                            while (z1 < 9999999){
                                z1 += 1000
                                kms1 += 1

                                if (titleStartPantographChet == kms1 && piketStartPantographChet == pkc1){
                                    faktNachKmPantographChet = z1
                                    piketNachKmPantographChet = pkc1
                                }
                            }

                            var z2 = 1201
                            var kms2 = 2
                            val pkc2 = 3

                            while (z2 < 9999999){
                                z2 += 1000
                                kms2 += 1

                                if (titleStartPantographChet == kms2 && piketStartPantographChet == pkc2){
                                    faktNachKmPantographChet = z2
                                    piketNachKmPantographChet = pkc2
                                }
                            }

                            var z3 = 1301
                            var kms3 = 2
                            val pkc3 = 4

                            while (z3 < 9999999){
                                z3 += 1000
                                kms3 += 1

                                if (titleStartPantographChet == kms3 && piketStartPantographChet == pkc3){
                                    faktNachKmPantographChet = z3
                                    piketNachKmPantographChet = pkc3
                                }
                            }

                            var z4 = 1401
                            var kms4 = 2
                            val pkc4 = 5

                            while (z4 < 9999999){
                                z4 += 1000
                                kms4 += 1

                                if (titleStartPantographChet == kms4 && piketStartPantographChet == pkc4){
                                    faktNachKmPantographChet = z4
                                    piketNachKmPantographChet = pkc4
                                }
                            }

                            var z5 = 1501
                            var kms5 = 2
                            val pkc5 = 6

                            while (z5 < 9999999){
                                z5 += 1000
                                kms5 += 1

                                if (titleStartPantographChet == kms5 && piketStartPantographChet == pkc5){
                                    faktNachKmPantographChet = z5
                                    piketNachKmPantographChet = pkc5
                                }
                            }

                            var z6 = 1601
                            var kms6 = 2
                            val pkc6 = 7

                            while (z6 < 9999999){
                                z6 += 1000
                                kms6 += 1

                                if (titleStartPantographChet == kms6 && piketStartPantographChet == pkc6){
                                    faktNachKmPantographChet = z6
                                    piketNachKmPantographChet = pkc6
                                }
                            }

                            var z7 = 1701
                            var kms7 = 2
                            val pkc7 = 8

                            while (z7 < 9999999){
                                z7 += 1000
                                kms7 += 1

                                if (titleStartPantographChet == kms7 && piketStartPantographChet == pkc7){
                                    faktNachKmPantographChet = z7
                                    piketNachKmPantographChet = pkc7
                                }
                            }

                            var z8 = 1801
                            var kms8 = 2
                            val pkc8 = 9

                            while (z8 < 9999999){
                                z8 += 1000
                                kms8 += 1

                                if (titleStartPantographChet == kms8 && piketStartPantographChet == pkc8){
                                    faktNachKmPantographChet = z8
                                    piketNachKmPantographChet = pkc8
                                }
                            }

                            var z9 = 1901
                            var kms9 = 2
                            val pkc9 = 10

                            while (z9 < 9999999){
                                z9 += 1000
                                kms9 += 1

                                if (titleStartPantographChet == kms9 && piketStartPantographChet == pkc9){
                                    faktNachKmPantographChet = z9
                                    piketNachKmPantographChet = pkc9
                                }
                            }

                            myPantograph.add(faktNachKmPantographChet)

//                            if (distanceChetFaktPantograph4500 > faktNachKmPantographChet && distanceChetFaktPantograph4500 < faktNachKmPantographChet + 48 && !isChekPantograph){
//                                myPantograph.add(faktNachKmPantographChet)
//                                isChekPantograph = true
//                            }
//
//                            if (distanceChetFaktPantograph4500 > faktNachKmPantographChet + 50 && distanceChetFaktPantograph4500 < faktNachKmPantographChet + 98){
//                                isChekPantograph = false
//                            }

                            if (distanceChet >= faktNachKmPantographChet - 3000 && distanceChet <= faktNachKmPantographChet - 2901){
                                faktNachKmPantograph3000 = faktNachKmPantographChet - 3000
                                faktNachKmPantograph2901 = faktNachKmPantographChet - 2901
                            }

                            if (distanceChet >= faktNachKmPantographChet - 2900 && distanceChet <= faktNachKmPantographChet - 2801){
                                faktNachKmPantograph2900 = faktNachKmPantographChet - 2900
                                faktNachKmPantograph2801 = faktNachKmPantographChet - 2801
                            }
                            if (distanceChet >= faktNachKmPantographChet - 2800 && distanceChet <= faktNachKmPantographChet - 2701){
                                faktNachKmPantograph2800 = faktNachKmPantographChet - 2800
                                faktNachKmPantograph2701 = faktNachKmPantographChet - 2701
                            }

                            if (distanceChet >= faktNachKmPantographChet - 2700 && distanceChet <= faktNachKmPantographChet - 2601){
                                faktNachKmPantograph2700 = faktNachKmPantographChet - 2700
                                faktNachKmPantograph2601 = faktNachKmPantographChet - 2601
                            }

                            if (distanceChet >= faktNachKmPantographChet - 2600 && distanceChet <= faktNachKmPantographChet - 2501){
                                faktNachKmPantograph2600 = faktNachKmPantographChet - 2600
                                faktNachKmPantograph2501 = faktNachKmPantographChet - 2501
                            }

                            if (distanceChet >= faktNachKmPantographChet - 2500 && distanceChet <= faktNachKmPantographChet - 2401){
                                faktNachKmPantograph2500 = faktNachKmPantographChet - 2500
                                faktNachKmPantograph2401 = faktNachKmPantographChet - 2401
                            }

                            if (distanceChet >= faktNachKmPantographChet - 2400 && distanceChet <= faktNachKmPantographChet - 2301){
                                faktNachKmPantograph2400 = faktNachKmPantographChet - 2400
                                faktNachKmPantograph2301 = faktNachKmPantographChet - 2301
                            }
                            if (distanceChet >= faktNachKmPantographChet - 2300 && distanceChet <= faktNachKmPantographChet - 2201){
                                faktNachKmPantograph2300 = faktNachKmPantographChet - 2300
                                faktNachKmPantograph2201 = faktNachKmPantographChet - 2201
                            }

                            if (distanceChet >= faktNachKmPantographChet - 2200 && distanceChet <= faktNachKmPantographChet - 2101){
                                faktNachKmPantograph2200 = faktNachKmPantographChet - 2200
                                faktNachKmPantograph2101 = faktNachKmPantographChet - 2101
                            }

                            if (distanceChet >= faktNachKmPantographChet - 2100 && distanceChet <= faktNachKmPantographChet - 2001){
                                faktNachKmPantograph2100 = faktNachKmPantographChet - 2100
                                faktNachKmPantograph2001 = faktNachKmPantographChet - 2001
                            }

                            // Конец расчёта начала Опускания токоприемников по киллометро


                        }
//                        myDbManagerPantograph.closeDb()
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        myBrake.clear()
                        myDbManagerBrake.openDb()
                        val dataListBrakes = myDbManagerBrake.readDbDataBrakeChet()
                        for (item in dataListBrakes){

                            titleStartBrakeChet = item.startChet
                            piketStartBrakeChet = item.picketStartChet

                            // Начало расчёта начала Торможения по киллометро

                            var z = 1001
                            var kms = 2
                            val pkc = 1

                            while (z < 9999999){
                                z += 1000
                                kms += 1

                                if (titleStartBrakeChet == kms && piketStartBrakeChet == pkc){
                                    faktNachKmBrakeChet = z
                                    piketNachKmBrakeChet = pkc
                                }
                            }

                            var z1 = 1101
                            var kms1 = 2
                            val pkc1 = 2

                            while (z1 < 9999999){
                                z1 += 1000
                                kms1 += 1

                                if (titleStartBrakeChet == kms1 && piketStartBrakeChet == pkc1){
                                    faktNachKmBrakeChet = z1
                                    piketNachKmBrakeChet = pkc1
                                }
                            }

                            var z2 = 1201
                            var kms2 = 2
                            val pkc2 = 3

                            while (z2 < 9999999){
                                z2 += 1000
                                kms2 += 1

                                if (titleStartBrakeChet == kms2 && piketStartBrakeChet == pkc2){
                                    faktNachKmBrakeChet = z2
                                    piketNachKmBrakeChet = pkc2
                                }
                            }

                            var z3 = 1301
                            var kms3 = 2
                            val pkc3 = 4

                            while (z3 < 9999999){
                                z3 += 1000
                                kms3 += 1

                                if (titleStartBrakeChet == kms3 && piketStartBrakeChet == pkc3){
                                    faktNachKmBrakeChet = z3
                                    piketNachKmBrakeChet = pkc3
                                }
                            }

                            var z4 = 1401
                            var kms4 = 2
                            val pkc4 = 5

                            while (z4 < 9999999){
                                z4 += 1000
                                kms4 += 1

                                if (titleStartBrakeChet == kms4 && piketStartBrakeChet == pkc4){
                                    faktNachKmBrakeChet = z4
                                    piketNachKmBrakeChet = pkc4
                                }
                            }

                            var z5 = 1501
                            var kms5 = 2
                            val pkc5 = 6

                            while (z5 < 9999999){
                                z5 += 1000
                                kms5 += 1

                                if (titleStartBrakeChet == kms5 && piketStartBrakeChet == pkc5){
                                    faktNachKmBrakeChet = z5
                                    piketNachKmBrakeChet = pkc5
                                }
                            }

                            var z6 = 1601
                            var kms6 = 2
                            val pkc6 = 7

                            while (z6 < 9999999){
                                z6 += 1000
                                kms6 += 1

                                if (titleStartBrakeChet == kms6 && piketStartBrakeChet == pkc6){
                                    faktNachKmBrakeChet = z6
                                    piketNachKmBrakeChet = pkc6
                                }
                            }

                            var z7 = 1701
                            var kms7 = 2
                            val pkc7 = 8

                            while (z7 < 9999999){
                                z7 += 1000
                                kms7 += 1

                                if (titleStartBrakeChet == kms7 && piketStartBrakeChet == pkc7){
                                    faktNachKmBrakeChet = z7
                                    piketNachKmBrakeChet = pkc7
                                }
                            }

                            var z8 = 1801
                            var kms8 = 2
                            val pkc8 = 9

                            while (z8 < 9999999){
                                z8 += 1000
                                kms8 += 1

                                if (titleStartBrakeChet == kms8 && piketStartBrakeChet == pkc8){
                                    faktNachKmBrakeChet = z8
                                    piketNachKmBrakeChet = pkc8
                                }
                            }

                            var z9 = 1901
                            var kms9 = 2
                            val pkc9 = 10

                            while (z9 < 9999999){
                                z9 += 1000
                                kms9 += 1

                                if (titleStartBrakeChet == kms9 && piketStartBrakeChet == pkc9){
                                    faktNachKmBrakeChet = z9
                                    piketNachKmBrakeChet = pkc9
                                }
                            }

                            myBrake.add(faktNachKmBrakeChet)

//                            if (distanceChetFaktBrake3500 > faktNachKmBrakeChet && distanceChetFaktBrake3500 < faktNachKmBrakeChet + 48 && !isChekBrake){
//                                myBrake.add(faktNachKmBrakeChet)
//                                isChekBrake = true
//                            }
//
//                            if (distanceChetFaktBrake3500 > faktNachKmBrakeChet + 50 && distanceChetFaktBrake3500 < faktNachKmBrakeChet + 98){
//                                isChekBrake = false
//                            }

                            if (distanceChet >= faktNachKmBrakeChet - 4000 && distanceChet <= faktNachKmBrakeChet - 3901){
                                faktNachKmBrake4000 = faktNachKmBrakeChet - 4000
                                faktNachKmBrake3901 = faktNachKmBrakeChet - 3901
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 3900 && distanceChet <= faktNachKmBrakeChet - 3801){
                                faktNachKmBrake3900 = faktNachKmBrakeChet - 3900
                                faktNachKmBrake3801 = faktNachKmBrakeChet - 3801
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 3800 && distanceChet <= faktNachKmBrakeChet - 3701){
                                faktNachKmBrake3800 = faktNachKmBrakeChet - 3800
                                faktNachKmBrake3701 = faktNachKmBrakeChet - 3701
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 3700 && distanceChet <= faktNachKmBrakeChet - 3601){
                                faktNachKmBrake3700 = faktNachKmBrakeChet - 3700
                                faktNachKmBrake3601 = faktNachKmBrakeChet - 3601
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 3600 && distanceChet <= faktNachKmBrakeChet - 3501){
                                faktNachKmBrake3600 = faktNachKmBrakeChet - 3600
                                faktNachKmBrake3501 = faktNachKmBrakeChet - 3501
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 2900 && distanceChet <= faktNachKmBrakeChet - 2801){
                                faktNachKmBrake2900 = faktNachKmBrakeChet - 2900
                                faktNachKmBrake2801 = faktNachKmBrakeChet - 2801
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 2800 && distanceChet <= faktNachKmBrakeChet - 2701){
                                faktNachKmBrake2800 = faktNachKmBrakeChet - 2800
                                faktNachKmBrake2701 = faktNachKmBrakeChet - 2701
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 2700 && distanceChet <= faktNachKmBrakeChet - 2601){
                                faktNachKmBrake2700 = faktNachKmBrakeChet - 2700
                                faktNachKmBrake2601 = faktNachKmBrakeChet - 2601
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 2600 && distanceChet <= faktNachKmBrakeChet - 2501){
                                faktNachKmBrake2600 = faktNachKmBrakeChet - 2600
                                faktNachKmBrake2501 = faktNachKmBrakeChet - 2501
                            }

                            if (distanceChet >= faktNachKmBrakeChet - 2500 && distanceChet <= faktNachKmBrakeChet - 2401){
                                faktNachKmBrake2500 = faktNachKmBrakeChet - 2500
                                faktNachKmBrake2401 = faktNachKmBrakeChet - 2401
                            }

                            // Конец расчёта начала Торможения по киллометро



                        }
//                        myDbManagerBrake.closeDb()
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        myLimitation.clear()
                        myDbManagerLimitations.openDb()
                        val dataListLimitations = myDbManagerLimitations.readDbDataLimitationsChet()
                        sumCalculateUslChet = (uslChet * 14) + 50
                        for (item in dataListLimitations){
                            titleStartChet = item.startChet
                            piketStartChet = item.picketStartChet
                            titleFinishChet = item.finishChet
                            piketFinishChet = item.picketFinishChet
                            speedChet = item.speedChet

                            // Расчёт начала ограничения по киллометро

                            var z = 1001
                            var kms = 2
                            val pkc = 1

                            while (z < 9999999){
                                z += 1000
                                kms += 1

                                if (titleStartChet == kms && piketStartChet == pkc){
                                    faktNachKmChet = z
                                    piketNachKmChet = pkc
                                }
                            }

                            var z1 = 1101
                            var kms1 = 2
                            val pkc1 = 2

                            while (z1 < 9999999){
                                z1 += 1000
                                kms1 += 1

                                if (titleStartChet == kms1 && piketStartChet == pkc1){
                                    faktNachKmChet = z1
                                    piketNachKmChet = pkc1
                                }
                            }

                            var z2 = 1201
                            var kms2 = 2
                            val pkc2 = 3

                            while (z2 < 9999999){
                                z2 += 1000
                                kms2 += 1

                                if (titleStartChet == kms2 && piketStartChet == pkc2){
                                    faktNachKmChet = z2
                                    piketNachKmChet = pkc2
                                }
                            }

                            var z3 = 1301
                            var kms3 = 2
                            val pkc3 = 4

                            while (z3 < 9999999){
                                z3 += 1000
                                kms3 += 1

                                if (titleStartChet == kms3 && piketStartChet == pkc3){
                                    faktNachKmChet = z3
                                    piketNachKmChet = pkc3
                                }
                            }

                            var z4 = 1401
                            var kms4 = 2
                            val pkc4 = 5

                            while (z4 < 9999999){
                                z4 += 1000
                                kms4 += 1

                                if (titleStartChet == kms4 && piketStartChet == pkc4){
                                    faktNachKmChet = z4
                                    piketNachKmChet = pkc4
                                }
                            }

                            var z5 = 1501
                            var kms5 = 2
                            val pkc5 = 6

                            while (z5 < 9999999){
                                z5 += 1000
                                kms5 += 1

                                if (titleStartChet == kms5 && piketStartChet == pkc5){
                                    faktNachKmChet = z5
                                    piketNachKmChet = pkc5
                                }
                            }

                            var z6 = 1601
                            var kms6 = 2
                            val pkc6 = 7

                            while (z6 < 9999999){
                                z6 += 1000
                                kms6 += 1

                                if (titleStartChet == kms6 && piketStartChet == pkc6){
                                    faktNachKmChet = z6
                                    piketNachKmChet = pkc6
                                }
                            }

                            var z7 = 1701
                            var kms7 = 2
                            val pkc7 = 8

                            while (z7 < 9999999){
                                z7 += 1000
                                kms7 += 1

                                if (titleStartChet == kms7 && piketStartChet == pkc7){
                                    faktNachKmChet = z7
                                    piketNachKmChet = pkc7
                                }
                            }

                            var z8 = 1801
                            var kms8 = 2
                            val pkc8 = 9

                            while (z8 < 9999999){
                                z8 += 1000
                                kms8 += 1

                                if (titleStartChet == kms8 && piketStartChet == pkc8){
                                    faktNachKmChet = z8
                                    piketNachKmChet = pkc8
                                }
                            }

                            var z9 = 1901
                            var kms9 = 2
                            val pkc9 = 10

                            while (z9 < 9999999){
                                z9 += 1000
                                kms9 += 1

                                if (titleStartChet == kms9 && piketStartChet == pkc9){
                                    faktNachKmChet = z9
                                    piketNachKmChet = pkc9
                                }
                            }

                            // Конец расчёта начала ограничения по киллометро

                            // Расчёт конца ограничения по киллометро

                            var x = 1099
                            var kmx = 2
                            val pkx = 1
                            while (x < 9999999){
                                x += 1000
                                kmx += 1

                                if (titleFinishChet == kmx && piketFinishChet == pkx){
                                    faktEndKmChet = x
                                    piketEndKmChet = pkx
                                }
                            }

                            var x1 = 1199
                            var kmx1 = 2
                            val pkx1 = 2
                            while (x1 < 9999999){
                                x1 += 1000
                                kmx1 += 1

                                if (titleFinishChet == kmx1 && piketFinishChet == pkx1){
                                    faktEndKmChet = x1
                                    piketEndKmChet = pkx1
                                }
                            }

                            var x2 = 1299
                            var kmx2 = 2
                            val pkx2 = 3
                            while (x2 < 9999999){
                                x2 += 1000
                                kmx2 += 1

                                if (titleFinishChet == kmx2 && piketFinishChet == pkx2){
                                    faktEndKmChet = x2
                                    piketEndKmChet = pkx2
                                }
                            }

                            var x3 = 1399
                            var kmx3 = 2
                            val pkx3 = 4
                            while (x3 < 9999999){
                                x3 += 1000
                                kmx3 += 1

                                if (titleFinishChet == kmx3 && piketFinishChet == pkx3){
                                    faktEndKmChet = x3
                                    piketEndKmChet = pkx3
                                }
                            }

                            var x4 = 1499
                            var kmx4 = 2
                            val pkx4 = 5
                            while (x4 < 9999999){
                                x4 += 1000
                                kmx4 += 1

                                if (titleFinishChet == kmx4 && piketFinishChet == pkx4){
                                    faktEndKmChet = x4
                                    piketEndKmChet = pkx4
                                }
                            }

                            var x5 = 1599
                            var kmx5 = 2
                            val pkx5 = 6
                            while (x5 < 9999999){
                                x5 += 1000
                                kmx5 += 1

                                if (titleFinishChet == kmx5 && piketFinishChet == pkx5){
                                    faktEndKmChet = x5
                                    piketEndKmChet = pkx5
                                }
                            }

                            var x6 = 1699
                            var kmx6 = 2
                            val pkx6 = 7
                            while (x6 < 9999999){
                                x6 += 1000
                                kmx6 += 1

                                if (titleFinishChet == kmx6 && piketFinishChet == pkx6){
                                    faktEndKmChet = x6
                                    piketEndKmChet = pkx6
                                }
                            }

                            var x7 = 1799
                            var kmx7 = 2
                            val pkx7 = 8
                            while (x7 < 9999999){
                                x7 += 1000
                                kmx7 += 1

                                if (titleFinishChet == kmx7 && piketFinishChet == pkx7){
                                    faktEndKmChet = x7
                                    piketEndKmChet = pkx7
                                }
                            }

                            var x8 = 1899
                            var kmx8 = 2
                            val pkx8 = 9
                            while (x8 < 9999999){
                                x8 += 1000
                                kmx8 += 1

                                if (titleFinishChet == kmx8 && piketFinishChet == pkx8){
                                    faktEndKmChet = x8
                                    piketEndKmChet = pkx8
                                }
                            }

                            var x9 = 1999
                            var kmx9 = 2
                            val pkx9 = 10
                            while (x9 < 9999999){
                                x9 += 1000
                                kmx9 += 1

                                if (titleFinishChet == kmx9 && piketFinishChet == pkx9){
                                    faktEndKmChet = x9
                                    piketEndKmChet = pkx9
                                }
                            }
                            myLimitation.add(faktNachKmChet)

//                            if (distanceChetFaktLimitation2500 > faktNachKmChet && distanceChetFaktLimitation2500 < faktNachKmChet + 48 && !isChekLimitation){
//                                myLimitation.add(faktNachKmChet)
//                                isChekLimitation = true
//                            }
//
//                            if (distanceChetFaktLimitation2500 > faktNachKmChet + 50 && distanceChetFaktLimitation2500 < faktNachKmChet + 98){
//                                isChekLimitation = false
//                            }



                            // Конец расчёта конца ограничения по киллометро

                            // Оповещение включения саут

                            if (distanceChet >= 6953452 - 1 && distanceChet <= 6953500 - 1 && isSautChet15 && isSautChet25 && isSautChet40 && isSautChet50
                                && isSautChet55 && isSautChet60 && isSautChet65 && isSautChet70 && isSautChet75 && isSautChet2){
                                playSound(saut)
                                isSautChet2 = !isSautChet2
                            }
                            if (distanceChet >= 6953552 - 1 && distanceChet <= 6953600 - 1 && isSautChet15 && isSautChet25 && isSautChet40 && isSautChet50
                                && isSautChet55 && isSautChet60 && isSautChet65 && isSautChet70 && isSautChet75 && isSautChet2){
                                playSound(saut)
                                isSautChet2 = !isSautChet2
                            }
                            if (distanceChet >= 6953652 - 1 && distanceChet <= 6953700 - 1 && isSautChet15 && isSautChet25 && isSautChet40 && isSautChet50
                                && isSautChet55 && isSautChet60 && isSautChet65 && isSautChet70 && isSautChet75 && isSautChet2){
                                playSound(saut)
                                isSautChet2 = !isSautChet2
                            }
                            if (distanceChet >= 6953752 - 1 && distanceChet <= 6953800 - 1 && isSautChet15 && isSautChet25 && isSautChet40 && isSautChet50
                                && isSautChet55 && isSautChet60 && isSautChet65 && isSautChet70 && isSautChet75 && isSautChet2){
                                playSound(saut)
                                isSautChet2 = !isSautChet2
                            }
                            if (distanceChet >= 6953852 - 1 && distanceChet <= 6953900 - 1 && isSautChet15 && isSautChet25 && isSautChet40 && isSautChet50
                                && isSautChet55 && isSautChet60 && isSautChet65 && isSautChet70 && isSautChet75 && isSautChet2){
                                playSound(saut)
                                isSautChet2 = !isSautChet2
                            }
                            if (distanceChet >= 6953952 - 1 && distanceChet <= 6954000 - 1 && isSautChet15 && isSautChet25 && isSautChet40 && isSautChet50
                                && isSautChet55 && isSautChet60 && isSautChet65 && isSautChet70 && isSautChet75 && isSautChet2){
                                playSound(saut)
                                isSautChet2 = !isSautChet2
                            }
                            if (distanceChet >= 6954002 - 1 && distanceChet <= 6954050 - 1){
                                isSautChet2 = true
                            }

                            // Начало оповещения ограничения скорости за 2 км/ч

                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet - 1901){
                                faktNachKm2000 = faktNachKmChet - 2000
                                faktNachKm1901 = faktNachKmChet - 1901

                                val arrayListSpeed = mutableListOf(int15, int25, int40, int50, int55, int60, int65, int70, int75)

                                if (speedChet == 15){
                                    int15 = item.speedChet
                                    arrayListSpeed.add(int15)
                                }
                                if (speedChet == 25){
                                    int25 = item.speedChet
                                    arrayListSpeed.add(int25)
                                }
                                if (speedChet == 40){
                                    int40 = item.speedChet
                                    arrayListSpeed.add(int40)
                                }
                                if (speedChet == 50){
                                    int50 = item.speedChet
                                    arrayListSpeed.add(int50)
                                }
                                if (speedChet == 55){
                                    int55 = item.speedChet
                                    arrayListSpeed.add(int55)
                                }
                                if (speedChet == 60){
                                    int60 = item.speedChet
                                    arrayListSpeed.add(int60)
                                }
                                if (speedChet == 65){
                                    int65 = item.speedChet
                                    arrayListSpeed.add(int65)
                                }
                                if (speedChet == 70){
                                    int70 = item.speedChet
                                    arrayListSpeed.add(int70)
                                }
                                if (speedChet == 75){
                                    int75 = item.speedChet
                                    arrayListSpeed.add(int75)
                                }

                                Log.d("MyLog", "minSpeed = ${arrayListSpeed.min()}")
                                speedChetMin = arrayListSpeed.min()
                            }

                            if (distanceChet >= faktNachKmChet - 1900 && distanceChet <= faktNachKmChet - 1801) {
                                faktNachKm1900 = faktNachKmChet - 1900
                                faktNachKm1801 = faktNachKmChet - 1801
                            }
                            if (distanceChet >= faktNachKmChet - 1800 && distanceChet <= faktNachKmChet - 1701) {
                                faktNachKm1800 = faktNachKmChet - 1800
                                faktNachKm1701 = faktNachKmChet - 1701
                            }
                            if (distanceChet >= faktNachKmChet - 1700 && distanceChet <= faktNachKmChet - 1601) {
                                faktNachKm1700 = faktNachKmChet - 1700
                                faktNachKm1601 = faktNachKmChet - 1601
                            }
                            if (distanceChet >= faktNachKmChet - 1600 && distanceChet <= faktNachKmChet - 1501) {
                                faktNachKm1600 = faktNachKmChet - 1600
                                faktNachKm1501 = faktNachKmChet - 1501
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet - 1999 && distanceChet <= faktNachKmChet - 1951 && item.speedChet == 15){
                                int15 = 150
                            }

                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 15){
                                int15 = 150
                                isLimitationsChet15 = true
                                isSautChet15 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------


                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 25){
                                int25 = 150
                                isLimitationsChet25 = true
                                isSautChet25 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------


                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 40){
                                int40 = 150
                                isLimitationsChet40 = true
                                isSautChet40 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------


                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 50){
                                int50 = 150
                                isLimitationsChet50 = true
                                isSautChet50 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------


                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 55){
                                int55 = 150
                                isLimitationsChet55 = true
                                isSautChet55 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 60){
                                int60 = 150
                                isLimitationsChet60 = true
                                isSautChet60 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 65){
                                int65 = 150
                                isLimitationsChet65 = true
                                isSautChet65 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 70){
                                int70 = 150
                                isLimitationsChet70 = true
                                isSautChet70 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet - 1899 && distanceChet <= faktNachKmChet - 1851 && item.speedChet == 75){
                                int75 = 150
                                isLimitationsChet75 = true
                                isSautChet75 = true
                            }
                            // Конец оповещения ограничения скорости за 2 км/ч


                            // Предупреждение о превышении за 400 метров до предупреждения

                            if (currentLocationChet.speed * 3.6 >= 15 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 15 && isLimitationsChet400m15){
                                playSound(voiceprev)
                                Log.d("MyLog", "faktNachKmChet15 = $faktNachKmChet")
                                isLimitationsChet400m15 = !isLimitationsChet400m15
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 15){
                                isLimitationsChet400m15 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 25 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 25 && isLimitationsChet400m25){
                                playSound(voiceprev)
                                Log.d("MyLog", "faktNachKmChet25 = $faktNachKmChet")
                                isLimitationsChet400m25 = !isLimitationsChet400m25
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 25){
                                isLimitationsChet400m25 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 40 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 40 && isLimitationsChet400m40){
                                playSound(voiceprev)
                                isLimitationsChet400m40 = !isLimitationsChet400m40
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 40){
                                isLimitationsChet400m40 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 50 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 50 && isLimitationsChet400m50){
                                playSound(voiceprev)
                                isLimitationsChet400m50 = !isLimitationsChet400m50
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 50){
                                isLimitationsChet400m50 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 55 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 55 && isLimitationsChet400m55){
                                playSound(voiceprev)
                                isLimitationsChet400m55 = !isLimitationsChet400m55
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 55){
                                isLimitationsChet400m55 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 60 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 60 && isLimitationsChet400m60){
                                playSound(voiceprev)
                                isLimitationsChet400m60 = !isLimitationsChet400m60
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 60){
                                isLimitationsChet400m60 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 65 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 65 && isLimitationsChet400m65){
                                playSound(voiceprev)
                                isLimitationsChet400m65 = !isLimitationsChet400m65
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 65){
                                isLimitationsChet400m65 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 70 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 70 && isLimitationsChet400m70){
                                playSound(voiceprev)
                                isLimitationsChet400m70 = !isLimitationsChet400m70
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 70){
                                isLimitationsChet400m70 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationChet.speed * 3.6 >= 75 && distanceChet >= faktNachKmChet - 549 && distanceChet <= faktNachKmChet - 501 && item.speedChet == 75 && isLimitationsChet400m75){
                                playSound(voiceprev)
                                isLimitationsChet400m75 = !isLimitationsChet400m75
                            }
                            if (distanceChet >= faktNachKmChet - 499 && distanceChet <= faktNachKmChet - 451 && item.speedChet == 75){
                                isLimitationsChet400m75 = true
                            }

                            // Конец предупреждение о превышении за 400 метров до предупреждения

                            // Предупреждение о превышении ограничения скорости

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 15){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 13 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 15 && isOgr){
                                    playSound(ogr15)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 15){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 15){
                                    val nachKm15 = faktNachKmChet
                                    val endKm15 = faktEndKmChet
                                    val speed15 = item.speedChet
                                    if (distanceChet >= nachKm15 - 2000 && distanceChet <= endKm15 + sumCalculateUslChet && speed15 == 15){
                                        tvOgrChet15 = "$speedChet"
                                        tvKmPkChet15 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 15){
                                tvOgrChet15 = ""
                                tvKmPkChet15 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------


                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 25){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 23 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 25 && isOgr){
                                    playSound(ogr25)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 25){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 25){
                                    val nachKm25 = faktNachKmChet
                                    val endKm25 = faktEndKmChet
                                    val speed25 = item.speedChet
                                    if (distanceChet >= nachKm25 - 2000 && distanceChet <= endKm25 + sumCalculateUslChet && speed25 == 25){
                                        tvOgrChet25 = "$speedChet"
                                        tvKmPkChet25 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 25){
                                tvOgrChet25 = ""
                                tvKmPkChet25 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 40){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 38 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 40 && isOgr){
                                    playSound(ogr40)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 40){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 40){
                                    val nachKm40 = faktNachKmChet
                                    val endKm40 = faktEndKmChet
                                    val speed40 = item.speedChet
                                    if (distanceChet >= nachKm40 - 2000 && distanceChet <= endKm40 + sumCalculateUslChet && speed40 == 40){
                                        tvOgrChet40 = "$speedChet"
                                        tvKmPkChet40 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 40){
                                tvOgrChet40 = ""
                                tvKmPkChet40 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 50){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 48 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 50 && isOgr){
                                    playSound(ogr50)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 50){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 50){
                                    val nachKm50 = faktNachKmChet
                                    val endKm50 = faktEndKmChet
                                    val speed50 = item.speedChet
                                    if (distanceChet >= nachKm50 - 2000 && distanceChet <= endKm50 + sumCalculateUslChet && speed50 == 50){
                                        tvOgrChet50 = "$speedChet"
                                        tvKmPkChet50 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 50){
                                tvOgrChet50 = ""
                                tvKmPkChet50 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 55){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 53 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 55 && isOgr){
                                    playSound(ogr55)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 55){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 55){
                                    val nachKm55 = faktNachKmChet
                                    val endKm55 = faktEndKmChet
                                    val speed55 = item.speedChet
                                    if (distanceChet >= nachKm55 - 2000 && distanceChet <= endKm55 + sumCalculateUslChet && speed55 == 55){
                                        tvOgrChet55 = "$speedChet"
                                        tvKmPkChet55 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 55){
                                tvOgrChet55 = ""
                                tvKmPkChet55 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 60){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 58 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 60 && isOgr){
                                    playSound(ogr60)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 60){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 60){
                                    val nachKm60 = faktNachKmChet
                                    val endKm60 = faktEndKmChet
                                    val speed60 = item.speedChet
                                    if (distanceChet >= nachKm60 - 2000 && distanceChet <= endKm60 + sumCalculateUslChet && speed60 == 60){
                                        tvOgrChet60 = "$speedChet"
                                        tvKmPkChet60 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 60){
                                tvOgrChet60 = ""
                                tvKmPkChet60 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 65){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 63 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 65 && isOgr){
                                    playSound(ogr65)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 65){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 65){
                                    val nachKm65 = faktNachKmChet
                                    val endKm65 = faktEndKmChet
                                    val speed65 = item.speedChet
                                    if (distanceChet >= nachKm65 - 2000 && distanceChet <= endKm65 + sumCalculateUslChet && speed65 == 65){
                                        tvOgrChet65 = "$speedChet"
                                        tvKmPkChet65 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 65){
                                tvOgrChet65 = ""
                                tvKmPkChet65 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 70){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 68 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 70 && isOgr){
                                    playSound(ogr70)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 70){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 70){
                                    val nachKm70 = faktNachKmChet
                                    val endKm70 = faktEndKmChet
                                    val speed70 = item.speedChet
                                    if (distanceChet >= nachKm70 - 2000 && distanceChet <= endKm70 + sumCalculateUslChet && speed70 == 70){
                                        tvOgrChet70 = "$speedChet"
                                        tvKmPkChet70 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 70){
                                tvOgrChet70 = ""
                                tvKmPkChet70 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceChet >= faktNachKmChet && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 75){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (currentLocationChet.speed * 3.6 >= 73 && distanceChet >= faktNachKmChet && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 75 && isOgr){
                                    playSound(ogr75)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktEndKmChet + sumCalculateUslChet && item.speedChet == 75){
                                val sumDistance = (faktEndKmChet - faktNachKmChet) + sumCalculateUslChet
                                if (distanceChet >= faktNachKmChet - 2000 && distanceChet <= faktNachKmChet + sumDistance && item.speedChet == 75){
                                    val nachKm75 = faktNachKmChet
                                    val endKm75 = faktEndKmChet
                                    val speed75 = item.speedChet
                                    if (distanceChet >= nachKm75 - 2000 && distanceChet <= endKm75 + sumCalculateUslChet && speed75 == 75){
                                        tvOgrChet75 = "$speedChet"
                                        tvKmPkChet75 = "$titleStartChet км $piketStartChet пк - $titleFinishChet км $piketFinishChet пк"
                                    }
                                }
                            }
                            if (distanceChet >= faktEndKmChet + sumCalculateUslChet && distanceChet <= faktEndKmChet + sumCalculateUslChet + 50 && item.speedChet == 75){
                                tvOgrChet75 = ""
                                tvKmPkChet75 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------


                            // Конец предупреждения о превышении ограничения скорости
                        }
//                        myDbManagerLimitations.closeDb()
                    }
                    myDbManagerPantograph.closeDb()
                    myDbManagerBrake.closeDb()
                    myDbManagerLimitations.closeDb()

                }
                val locModelChetFakt = LocationModelChetFakt(
                    currentLocationChet.speed,
                    distanceChet,
                    kmDistanceChetFakt,
                    pkDistanceChetFakt,
                    tvOgrChet15,
                    tvOgrChet25,
                    tvOgrChet40,
                    tvOgrChet50,
                    tvOgrChet55,
                    tvOgrChet60,
                    tvOgrChet65,
                    tvOgrChet70,
                    tvOgrChet75,
                    tvKmPkChet15,
                    tvKmPkChet25,
                    tvKmPkChet40,
                    tvKmPkChet50,
                    tvKmPkChet55,
                    tvKmPkChet60,
                    tvKmPkChet65,
                    tvKmPkChet70,
                    tvKmPkChet75
                )
                sendLocDataChetFakt(locModelChetFakt)
            }
            lastLocationChetFakt = currentLocationChet
        }
    }

    private suspend fun calculationKmFakt() = withContext(Dispatchers.Main){
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
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q
                faktFinishKmChet = w
            }

            if (distanceChet >= q1 && distanceChet <= w1){
                qpk = 2
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q1
                faktFinishKmChet = w1
            }

            if (distanceChet >= q2 && distanceChet <= w2){
                qpk = 3
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q2
                faktFinishKmChet = w2
            }

            if (distanceChet >= q3 && distanceChet <= w3){
                qpk = 4
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q3
                faktFinishKmChet = w3
            }

            if (distanceChet >= q4 && distanceChet <= w4){
                qpk = 5
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q4
                faktFinishKmChet = w4
            }

            if (distanceChet >= q5 && distanceChet <= w5){
                qpk = 6
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q5
                faktFinishKmChet = w5
            }

            if (distanceChet >= q6 && distanceChet <= w6){
                qpk = 7
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q6
                faktFinishKmChet = w6
            }

            if (distanceChet >= q7 && distanceChet <= w7){
                qpk = 8
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q7
                faktFinishKmChet = w7
            }

            if (distanceChet >= q8 && distanceChet <= w8){
                qpk = 9
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q8
                faktFinishKmChet = w8
            }

            if (distanceChet >= q9 && distanceChet <= w9){
                qpk = 10
                kmDistanceChetFakt = qkm
                pkDistanceChetFakt = qpk

                faktStartKmChet = q9
                faktFinishKmChet = w9
            }
        }
    }

    private fun sendLocDataChetFakt(locModelChetFakt: LocationModelChetFakt){
        val iChetFakt = Intent(LOC_MODEL_INTENT_CHET_FAKT)
        iChetFakt.putExtra(LOC_MODEL_INTENT_CHET_FAKT, locModelChetFakt)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(iChetFakt)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startNotificationChetFakt(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nChannelChetFakt = NotificationChannel(
                CHANNEL_ID_CHET_FAKT,
                "Location Service Fakt",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManagerChetFakt = getSystemService(NotificationManager::class.java) as NotificationManager
            nManagerChetFakt.createNotificationChannel(nChannelChetFakt)
        }
        val nIntentChetFakt = Intent(this, ActivityChet::class.java)
        val pIntentChetFakt = PendingIntent.getActivity(
            this,
            21,
            nIntentChetFakt,
            PendingIntent.FLAG_MUTABLE
        )
        val notificationChetFakt = NotificationCompat.Builder(
            this,
            CHANNEL_ID_CHET_FAKT
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("GPS assistant Чётный запущен!")
            .setContentIntent(pIntentChetFakt).build()
        startForeground(91, notificationChetFakt)
    }

    private fun initLocationChetFakt(){
        locRequestChetFakt = LocationRequest.create()
        locRequestChetFakt.interval = 1000
        locRequestChetFakt.fastestInterval = 1000
        locRequestChetFakt.priority = PRIORITY_HIGH_ACCURACY
        locProviderChetFakt = LocationServices.getFusedLocationProviderClient(baseContext)

    }

    private fun startLocationUpdatesChetFakt(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) return

        locProviderChetFakt.requestLocationUpdates(
            locRequestChetFakt,
            locCallbackChetFakt,
            Looper.myLooper()
        )
    }

    companion object{
        const val LOC_MODEL_INTENT_CHET_FAKT = "loc_intent_chet_fakt"
        const val CHANNEL_ID_CHET_FAKT = "channel_chet_fakt"
        var isRunningChetFakt = false
    }

}