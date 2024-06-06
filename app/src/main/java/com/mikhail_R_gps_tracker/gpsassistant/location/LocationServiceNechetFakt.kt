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
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.MainFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.ViewTrackFragmentNechet.Companion.FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET_FAKT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Suppress("DEPRECATION")
class LocationServiceNechetFakt : Service() {
    private val distanceKmNechetFakt = ArrayList<ListLatLongKmNechetFakt>()
    private var distanceNechet: Float = 0.0f
    private var fragmentLatLongToServiceNechetFakt: String = ""
    private var isCheckTrackNechetFakt = true

    var kmDistanceNechetFakt: Int = 0
    var pkDistanceNechetFakt: Int = 0

    private var lastLocationNechetFakt: Location? = null
    private lateinit var locProviderNechetFakt: FusedLocationProviderClient
    private lateinit var locRequestNechetFakt: LocationRequest
    private var isDebagNechetFakt = true
    private var isOgr = true

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
    private var sautout: Int = 0
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
    private var uslNechet = 0.0F
    private var sumCalculateUslNechet = 1.0F
    private lateinit var myDbManagerLimitations: MyDbManagerLimitations

    private var isPantographNechet = true
    private var isBrakeNechet = true

    private var isChekBrakeNechet = true
    private var isChekPantographNechet = true

    private var isChekPantographNechet2 = true
    private var isChekBrakeNechet2 = true
    private var isChekPantographNechet3 = true

    private var isLimitationsNechet15 = true
    private var isLimitationsNechet25 = true
    private var isLimitationsNechet40 = true
    private var isLimitationsNechet50 = true
    private var isLimitationsNechet55 = true
    private var isLimitationsNechet60 = true
    private var isLimitationsNechet65 = true
    private var isLimitationsNechet70 = true
    private var isLimitationsNechet75 = true

    private var isLimitationsChet400m15 = true
    private var isLimitationsChet400m25 = true
    private var isLimitationsChet400m40 = true
    private var isLimitationsChet400m50 = true
    private var isLimitationsChet400m55 = true
    private var isLimitationsChet400m60 = true
    private var isLimitationsChet400m65 = true
    private var isLimitationsChet400m70 = true
    private var isLimitationsChet400m75 = true

    private var isSautNechet15 = true
    private var isSautNechet25 = true
    private var isSautNechet40 = true
    private var isSautNechet50 = true
    private var isSautNechet55 = true
    private var isSautNechet60 = true
    private var isSautNechet65 = true
    private var isSautNechet70 = true
    private var isSautNechet75 = true
    private var isSautNechet2 = true

    private var int15: Int = 150
    private var int25: Int = 150
    private var int40: Int = 150
    private var int50: Int = 150
    private var int55: Int = 150
    private var int60: Int = 150
    private var int65: Int = 150
    private var int70: Int = 150
    private var int75: Int = 150

    private lateinit var myDbManagerPantograph: MyDbManagerPantograph

    private lateinit var myDbManagerBrake: MyDbManagerBrake

    var tvOgrNechet15: String = ""
    var tvOgrNechet25: String = ""
    var tvOgrNechet40: String = ""
    var tvOgrNechet50: String = ""
    var tvOgrNechet55: String = ""
    var tvOgrNechet60: String = ""
    var tvOgrNechet65: String = ""
    var tvOgrNechet70: String = ""
    var tvOgrNechet75: String = ""

    var tvKmPkNechet15: String = ""
    var tvKmPkNechet25: String = ""
    var tvKmPkNechet40: String = ""
    var tvKmPkNechet50: String = ""
    var tvKmPkNechet55: String = ""
    var tvKmPkNechet60: String = ""
    var tvKmPkNechet65: String = ""
    var tvKmPkNechet70: String = ""
    var tvKmPkNechet75: String = ""

    private var faktStartKmNechet: Int = 0
    private var faktFinishKmNechet: Int = 0

    private var titleStartNechet: Int = 0
    private var piketStartNechet: Int = 0
    private var faktNachKmNechet: Int = 0
    private var piketNachKmNechet: Int = 0

    private var titleFinishNechet: Int = 0
    private var piketFinishNechet: Int = 0
    private var speedNechet: Int = 0
    private var speedNechetMin: Int = 0
    private var faktEndKmNechet: Int = 0
    private var piketEndKmNechet: Int = 0

    private var titleStartPantographNechet: Int = 0
    private var piketStartPantographNechet: Int = 0
    private var faktNachKmPantographNechet: Int = 0
    private var piketNachKmPantographNechet: Int = 0

    private var titleStartBrakeNechet: Int = 0
    private var piketStartBrakeNechet: Int = 0
    private var faktNachKmBrakeNechet: Int = 0
    private var piketNachKmBrakeNechet: Int = 0

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

    private val receiverUslNechet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentUslNechet: Intent?) {
            if (intentUslNechet?.action == MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_USL_NECHET){
                val fragmentModelUslNechet = intentUslNechet.getSerializableExtra(MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_USL_NECHET) as FragmentModelUslNechet
                uslNechet = fragmentModelUslNechet.mainUslChet
                Log.d("MyLog", "uslNechet: $uslNechet")

            }
        }
    }

    private fun registerLocReceiverUslNechet(){
        val locFilterUslNechet = IntentFilter(MainFragmentNechet.LOC_MODEL_INTENT_FRAGMENT_USL_NECHET)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverUslNechet, locFilterUslNechet)
    }

    private val receiverLatLongKmToServiceNechetFakt = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intentLatLongKmToServiceNechetFakt: Intent?) {
            if (intentLatLongKmToServiceNechetFakt?.action == FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET_FAKT){
                val fragmentLatLongKmToServiceNechetFakt = intentLatLongKmToServiceNechetFakt.getSerializableExtra(
                    FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET_FAKT) as FragmentLatLongKmToServiceNechet
                if (isCheckTrackNechetFakt && fragmentLatLongToServiceNechetFakt == "") {
                    fragmentLatLongToServiceNechetFakt = fragmentLatLongKmToServiceNechetFakt.fragmentLatLongKmToServiceNechet
                    Log.d("MyLog", "FragmentLatLongKmToServiceNechetFakt: $fragmentLatLongToServiceNechetFakt")
                    isCheckTrackNechetFakt = !isCheckTrackNechetFakt
                }
            }
        }
    }
    private fun registerLatLongKmToServiceNechetFakt(){
        val locFilterLatLongKmToServiceNechetFakt = IntentFilter(FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET_FAKT)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiverLatLongKmToServiceNechetFakt, locFilterLatLongKmToServiceNechetFakt)
    }

    private fun getLatLongKmNechetFakt() {
        if (distanceKmNechetFakt.indices.isEmpty()) {
            val list = fragmentLatLongToServiceNechetFakt.split("/")
            list.forEach {
                if (it.isEmpty()) return@forEach
                val points = it.split(",")
                distanceKmNechetFakt.add(ListLatLongKmNechetFakt(points[0].toFloat(), points[1].toFloat(), points[2].toFloat()))
            }
        }
    }

    data class ListLatLongKmNechetFakt(val lat: Float,
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
        registerLocReceiverUslNechet()
        registerLatLongKmToServiceNechetFakt()
        startNotificationNechetFakt()
        startLocationUpdatesNechetFakt()
        isRunningNechetFakt = true
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
//        return soundPool.load(afd.fileDescriptor, 1000, 1000000000000, 1)
        return soundPool.load(afd,1)
    }

    override fun onCreate() {
        super.onCreate()
        initLocationNechetFakt()

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
        sautout = loadSound("sautout.mp3")
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
        isRunningNechetFakt = false
        locProviderNechetFakt.removeLocationUpdates(locCallbackNechetFakt)
    }

    private val locCallbackNechetFakt = object : LocationCallback(){
        override fun onLocationResult(lResultNechetFakt: LocationResult) {
            super.onLocationResult(lResultNechetFakt)
            val currentLocationNechet = lResultNechetFakt.lastLocation
            if (lastLocationNechetFakt != null && currentLocationNechet != null){
                if (currentLocationNechet.speed > 0.4 || isDebagNechetFakt){

                    getLatLongKmNechetFakt()

                    val mLoc = Location("").apply {
                        latitude = currentLocationNechet.latitude
                        longitude = currentLocationNechet.longitude
                    }
                    var minDistance = 10000f
                    var distanceIndex = 0

                    for (i in distanceKmNechetFakt.indices){
                        val pointLoc = Location("").apply {
                            latitude = distanceKmNechetFakt[i].lat.toDouble()
                            longitude = distanceKmNechetFakt[i].long.toDouble()
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
                        distanceNechet = distanceKmNechetFakt[distanceIndex].distKm
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        calculationKmFakt()
                    }

                    if (distanceNechet < faktNachKm2000 && distanceNechet > faktNachKm1901){
                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000){
                            Log.d("MyLog", "Ни чему не равен!")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }
                        }
                        if (faktStartKmNechet == faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000 && isChekBrakeNechet2 && isChekPantographNechet2){
                            Log.d("MyLog", "Все равны!")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }

                            isChekBrakeNechet2 = !isChekBrakeNechet2
                            isChekPantographNechet2 = !isChekPantographNechet2
                            Log.d("MyLog", "isChekBrakeNechet2 = $isChekBrakeNechet2")
                            Log.d("MyLog", "isChekPantographNechet2 = $isChekPantographNechet2")
                        }

                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000 && isChekPantographNechet){
                            Log.d("MyLog", "faktNachKmPantograph3000 равен faktNachKm2000!8")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }

                            isChekPantographNechet = !isChekPantographNechet
                            Log.d("MyLog", "isChekPantographNechet = $isChekPantographNechet")
                        }

                        if (faktStartKmNechet == faktNachKmBrake4000 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000 && isChekBrakeNechet){
                            Log.d("MyLog", "faktNachKmBrake4000 равен faktNachKm2000!6")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }

                            isChekBrakeNechet = !isChekBrakeNechet
                        }
                    }

                    if (distanceNechet < faktNachKm2000 && distanceNechet > faktNachKm1901){
                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000){
                            Log.d("MyLog", "Ни чему не равен!")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }
                        }
                        if (faktStartKmNechet == faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000 && !isChekBrakeNechet2 && !isChekPantographNechet2){
                            Log.d("MyLog", "Все равны!")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }
                            Log.d("MyLog", "isChekBrakeNechet2 = $isChekBrakeNechet2")
                            Log.d("MyLog", "isChekPantographNechet2 = $isChekPantographNechet2")
                        }

                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000 && !isChekPantographNechet){
                            Log.d("MyLog", "faktNachKmPantograph3000 равен faktNachKm2000!8")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }
                            Log.d("MyLog", "isChekPantographNechet = $isChekPantographNechet")
                        }

                        if (faktStartKmNechet == faktNachKmBrake4000 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet == faktNachKm2000 && !isChekBrakeNechet){
                            Log.d("MyLog", "faktNachKmBrake4000 равен faktNachKm2000!6")
                            if (speedNechetMin == 15 && isLimitationsNechet15){
                                playSound(voice15)
                                isLimitationsNechet15 = !isLimitationsNechet15
                                isSautNechet15 = !isSautNechet15
                            }
                            if (speedNechetMin == 25 && isLimitationsNechet25){
                                playSound(voice25)
                                isLimitationsNechet25 = !isLimitationsNechet25
                                isSautNechet25 = !isSautNechet25
                            }
                            if (speedNechetMin == 40 && isLimitationsNechet40){
                                playSound(voice40)
                                isLimitationsNechet40 = !isLimitationsNechet40
                                isSautNechet40 = !isSautNechet40
                            }
                            if (speedNechetMin == 50 && isLimitationsNechet50){
                                playSound(voice50)
                                isLimitationsNechet50 = !isLimitationsNechet50
                                isSautNechet50 = !isSautNechet50
                            }
                            if (speedNechetMin == 55 && isLimitationsNechet55){
                                playSound(voice55)
                                isLimitationsNechet55 = !isLimitationsNechet55
                                isSautNechet55 = !isSautNechet55
                            }
                            if (speedNechetMin == 60 && isLimitationsNechet60){
                                playSound(voice60)
                                isLimitationsNechet60 = !isLimitationsNechet60
                                isSautNechet60 = !isSautNechet60
                            }
                            if (speedNechetMin == 65 && isLimitationsNechet65){
                                playSound(voice65)
                                isLimitationsNechet65 = !isLimitationsNechet65
                                isSautNechet65 = !isSautNechet65
                            }
                            if (speedNechetMin == 70 && isLimitationsNechet70){
                                playSound(voice70)
                                isLimitationsNechet70 = !isLimitationsNechet70
                                isSautNechet70 = !isSautNechet70
                            }
                            if (speedNechetMin == 75 && isLimitationsNechet75){
                                playSound(voice75)
                                isLimitationsNechet75 = !isLimitationsNechet75
                                isSautNechet75 = !isSautNechet75
                            }
                            Log.d("MyLog", "isChekBrakeNechet = $isChekBrakeNechet")
                        }
                    }

                    if (!isChekPantographNechet){
                        Log.d("MyLog", "Зашли")
                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph2900 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet = true
                            Log.d("MyLog", "isChekPantographNechet900 = $isChekPantographNechet")
                        }
                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph2800 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet = true
                            Log.d("MyLog", "isChekPantographNechet800 = $isChekPantographNechet")
                        }
                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph2700 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet = true
                            Log.d("MyLog", "isChekPantographNechet700 = $isChekPantographNechet")
                        }
                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph2600 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet = true
                            Log.d("MyLog", "isChekPantographNechet600 = $isChekPantographNechet")
                        }
                    }

                    if (!isChekBrakeNechet){
                        if (faktStartKmNechet == faktNachKmBrake3900 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000){

                            playSound(brake)
                            isChekBrakeNechet = true
                            Log.d("MyLog", "isChekBrakeNechet900 = $isChekBrakeNechet")
                        }
                        if (faktStartKmNechet == faktNachKmBrake3800 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000){

                            playSound(brake)
                            isChekBrakeNechet = true
                            Log.d("MyLog", "isChekBrakeNechet800 = $isChekBrakeNechet")
                        }
                        if (faktStartKmNechet == faktNachKmBrake3700 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000){

                            playSound(brake)
                            isChekBrakeNechet = true
                            Log.d("MyLog", "isChekBrakeNechet700 = $isChekBrakeNechet")
                        }
                        if (faktStartKmNechet == faktNachKmBrake3600 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000){

                            playSound(brake)
                            isChekBrakeNechet = true
                            Log.d("MyLog", "isChekBrakeNechet600 = $isChekBrakeNechet")
                        }
                    }

                    if (!isChekPantographNechet2){
                        if (faktStartKmNechet == faktNachKmPantograph2500 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet2 = true
                            Log.d("MyLog", "isChekPantographNechet2500 = $isChekPantographNechet2")
                        }
                        if (faktStartKmNechet == faktNachKmPantograph2400 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet2 = true
                            Log.d("MyLog", "isChekPantographNechet2400 = $isChekPantographNechet2")
                        }
                        if (faktStartKmNechet == faktNachKmPantograph2300 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet2 = true
                            Log.d("MyLog", "isChekPantographNechet2300 = $isChekPantographNechet2")
                        }
                        if (faktStartKmNechet == faktNachKmPantograph2200 && faktStartKmNechet != faktNachKm2000){

                            playSound(pantograph)
                            isChekPantographNechet2 = true
                            Log.d("MyLog", "isChekPantographNechet2200 = $isChekPantographNechet2")
                        }
                    }

                    if (!isChekBrakeNechet2){
                        if (isChekPantographNechet2){
                            if (faktStartKmNechet == faktNachKmBrake2900 && faktStartKmNechet != faktNachKm2000){

                                playSound(brake)
                                isChekBrakeNechet2 = true
                                Log.d("MyLog", "faktNachKmBrake2900 = $isChekBrakeNechet2")
                            }
                            if (faktStartKmNechet == faktNachKmBrake2800 && faktStartKmNechet != faktNachKm2000){

                                playSound(brake)
                                isChekBrakeNechet2 = true
                                Log.d("MyLog", "faktNachKmBrake2800 = $isChekBrakeNechet2")
                            }
                            if (faktStartKmNechet == faktNachKmBrake2700 && faktStartKmNechet != faktNachKm2000){

                                playSound(brake)
                                isChekBrakeNechet2 = true
                                Log.d("MyLog", "faktNachKmBrake2700 = $isChekBrakeNechet2")
                            }
                            if (faktStartKmNechet == faktNachKmBrake2600 && faktStartKmNechet != faktNachKm2000){

                                playSound(brake)
                                isChekBrakeNechet2 = true
                                Log.d("MyLog", "faktNachKmBrake2600 = $isChekBrakeNechet2")
                            }
                            if (faktStartKmNechet == faktNachKmBrake2500 && faktStartKmNechet != faktNachKm2000){

                                playSound(brake)
                                isChekBrakeNechet2 = true
                                Log.d("MyLog", "faktNachKmBrake2500 = $isChekBrakeNechet2")
                            }
                        }
                    }

                    if (distanceNechet < faktNachKmPantograph3000 && distanceNechet > faktNachKmPantograph2901){
                        if (faktStartKmNechet != faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000 && isPantographNechet){
                            Log.d("MyLog", "faktStartKmNechet = $faktStartKmNechet")
                            Log.d("MyLog", "faktNachKmBrake4000 = $faktNachKmBrake4000")
                            Log.d("MyLog", "faktNachKmPantograph3000 = $faktNachKmPantograph3000")
                            Log.d("MyLog", "faktNachKm2000 = $faktNachKm2000")
                            playSound(pantograph)
                            isPantographNechet = !isPantographNechet
                            Log.d("MyLog", "isPantographNechet = $isPantographNechet")
                        }
                        if (faktStartKmNechet == faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000 && isChekPantographNechet3){
                            playSound(pantograph)
                            isChekPantographNechet3 = !isChekPantographNechet3
                            Log.d("MyLog", "isChekPantographNechet3 = $isChekPantographNechet3")
                        }
                    }
                    if (distanceNechet < faktNachKmPantograph2901 && distanceNechet > faktNachKmPantograph2801 && !isPantographNechet){
                        isPantographNechet = true
                        Log.d("MyLog", "isPantographNechet = $isPantographNechet")
                    }
                    if (distanceNechet < faktNachKmPantograph2901 && distanceNechet > faktNachKmPantograph2801 && !isChekPantographNechet3){
                        isChekPantographNechet3 = true
                        Log.d("MyLog", "isChekPantographNechet3 = $isChekPantographNechet3")
                    }

                    if (distanceNechet < faktNachKmBrake4000 && distanceNechet > faktNachKmBrake3901){
                        if (faktStartKmNechet == faktNachKmBrake4000 && faktStartKmNechet != faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000 && isBrakeNechet){
                            Log.d("MyLog", "faktStartKmNechet = $faktStartKmNechet")
                            Log.d("MyLog", "faktNachKmBrake4000 = $faktNachKmBrake4000")
                            Log.d("MyLog", "faktNachKmPantograph3000 = $faktNachKmPantograph3000")
                            Log.d("MyLog", "faktNachKm2000 = $faktNachKm2000")
                            playSound(brake)
                            isBrakeNechet = !isBrakeNechet
                            Log.d("MyLog", "isBrakeNechet = $isBrakeNechet")
                        }
                        if (faktStartKmNechet == faktNachKmBrake4000 && faktStartKmNechet == faktNachKmPantograph3000 && faktStartKmNechet != faktNachKm2000 && isChekBrakeNechet){
                            isChekBrakeNechet = !isChekBrakeNechet
                            Log.d("MyLog", "isChekBrakeNechet = $isChekBrakeNechet")
                        }
                    }
                    if (distanceNechet < faktNachKmBrake3901 && distanceNechet > faktNachKmBrake3801){
                        isBrakeNechet = true
                        Log.d("MyLog", "isBrakeNechet = $isBrakeNechet")
                    }

                    // Конец оповещения

                    CoroutineScope(Dispatchers.Main).launch {
                        myDbManagerPantograph.openDb()
                        val dataListPantographs = myDbManagerPantograph.readDbDataPantographNechet()
                        for (item in dataListPantographs){

                            titleStartPantographNechet = item.startNechet
                            piketStartPantographNechet = item.picketStartNechet

                            // Начало расчёта начала Опускания токоприемников по киллометро

                            var x = 9999999
                            var kmx = 10000
                            val pkx = 10
                            while (x > 0){
                                x -= 1000
                                kmx -= 1

                                if (titleStartPantographNechet == kmx && piketStartPantographNechet == pkx){
                                    faktNachKmPantographNechet = x
                                    piketNachKmPantographNechet = pkx
                                }
                            }

                            var x1 = 9999899
                            var kmx1 = 10000
                            val pkx1 = 9
                            while (x1 > 0){
                                x1 -= 1000
                                kmx1 -= 1

                                if (titleStartPantographNechet == kmx1 && piketStartPantographNechet == pkx1){
                                    faktNachKmPantographNechet = x1
                                    piketNachKmPantographNechet = pkx1
                                }
                            }

                            var x2 = 9999799
                            var kmx2 = 10000
                            val pkx2 = 8
                            while (x2 > 0){
                                x2 -= 1000
                                kmx2 -= 1

                                if (titleStartPantographNechet == kmx2 && piketStartPantographNechet == pkx2){
                                    faktNachKmPantographNechet = x2
                                    piketNachKmPantographNechet = pkx2
                                }
                            }

                            var x3 = 9999699
                            var kmx3 = 10000
                            val pkx3 = 7
                            while (x3 > 0){
                                x3 -= 1000
                                kmx3 -= 1

                                if (titleStartPantographNechet == kmx3 && piketStartPantographNechet == pkx3){
                                    faktNachKmPantographNechet = x3
                                    piketNachKmPantographNechet = pkx3
                                }
                            }

                            var x4 = 9999599
                            var kmx4 = 10000
                            val pkx4 = 6
                            while (x4 > 0){
                                x4 -= 1000
                                kmx4 -= 1

                                if (titleStartPantographNechet == kmx4 && piketStartPantographNechet == pkx4){
                                    faktNachKmPantographNechet = x4
                                    piketNachKmPantographNechet = pkx4
                                }
                            }

                            var x5 = 9999499
                            var kmx5 = 10000
                            val pkx5 = 5
                            while (x5 > 0){
                                x5 -= 1000
                                kmx5 -= 1

                                if (titleStartPantographNechet == kmx5 && piketStartPantographNechet == pkx5){
                                    faktNachKmPantographNechet = x5
                                    piketNachKmPantographNechet = pkx5
                                }
                            }

                            var x6 = 9999399
                            var kmx6 = 10000
                            val pkx6 = 4
                            while (x6 > 0){
                                x6 -= 1000
                                kmx6 -= 1

                                if (titleStartPantographNechet == kmx6 && piketStartPantographNechet == pkx6){
                                    faktNachKmPantographNechet = x6
                                    piketNachKmPantographNechet = pkx6
                                }
                            }

                            var x7 = 9999299
                            var kmx7 = 10000
                            val pkx7 = 3
                            while (x7 > 0){
                                x7 -= 1000
                                kmx7 -= 1

                                if (titleStartPantographNechet == kmx7 && piketStartPantographNechet == pkx7){
                                    faktNachKmPantographNechet = x7
                                    piketNachKmPantographNechet = pkx7
                                }
                            }

                            var x8 = 9999199
                            var kmx8 = 10000
                            val pkx8 = 2
                            while (x8 > 0){
                                x8 -= 1000
                                kmx8 -= 1

                                if (titleStartPantographNechet == kmx8 && piketStartPantographNechet == pkx8){
                                    faktNachKmPantographNechet = x8
                                    piketNachKmPantographNechet = pkx8
                                }
                            }

                            var x9 = 9999099
                            var kmx9 = 10000
                            val pkx9 = 1
                            while (x9 > 0){
                                x9 -= 1000
                                kmx9 -= 1

                                if (titleStartPantographNechet == kmx9 && piketStartPantographNechet == pkx9){
                                    faktNachKmPantographNechet = x9
                                    piketNachKmPantographNechet = pkx9
                                }
                            }

                            // Конец расчёта начала Опускания токоприемников по киллометро

                            // Начало оповещения опускания токоприемников

                            if (distanceNechet <= faktNachKmPantographNechet + 3000 && distanceNechet >= faktNachKmPantographNechet + 2901){
                                faktNachKmPantograph3000 = faktNachKmPantographNechet + 3000
                                faktNachKmPantograph2901 = faktNachKmPantographNechet + 2901
                            }

                            if (distanceNechet <= faktNachKmPantographNechet + 2900 && distanceNechet >= faktNachKmPantographNechet + 2801){
                                faktNachKmPantograph2900 = faktNachKmPantographNechet + 2900
                                faktNachKmPantograph2801 = faktNachKmPantographNechet + 2801
                            }
                            if (distanceNechet <= faktNachKmPantographNechet + 2800 && distanceNechet >= faktNachKmPantographNechet + 2701){
                                faktNachKmPantograph2800 = faktNachKmPantographNechet + 2800
                                faktNachKmPantograph2701 = faktNachKmPantographNechet + 2701
                            }

                            if (distanceNechet <= faktNachKmPantographNechet + 2700 && distanceNechet >= faktNachKmPantographNechet + 2601){
                                faktNachKmPantograph2700 = faktNachKmPantographNechet + 2700
                                faktNachKmPantograph2601 = faktNachKmPantographNechet + 2601
                            }

                            if (distanceNechet <= faktNachKmPantographNechet + 2600 && distanceNechet >= faktNachKmPantographNechet + 2501){
                                faktNachKmPantograph2600 = faktNachKmPantographNechet + 2600
                                faktNachKmPantograph2501 = faktNachKmPantographNechet + 2501
                            }

                            if (distanceNechet <= faktNachKmPantographNechet + 2500 && distanceNechet >= faktNachKmPantographNechet + 2401){
                                faktNachKmPantograph2500 = faktNachKmPantographNechet + 2500
                                faktNachKmPantograph2401 = faktNachKmPantographNechet + 2401
                            }

                            if (distanceNechet <= faktNachKmPantographNechet + 2400 && distanceNechet >= faktNachKmPantographNechet + 2301){
                                faktNachKmPantograph2400 = faktNachKmPantographNechet + 2400
                                faktNachKmPantograph2301 = faktNachKmPantographNechet + 2301
                            }
                            if (distanceNechet <= faktNachKmPantographNechet + 2300 && distanceNechet >= faktNachKmPantographNechet + 2201){
                                faktNachKmPantograph2300 = faktNachKmPantographNechet + 2300
                                faktNachKmPantograph2201 = faktNachKmPantographNechet + 2201
                            }

                            if (distanceNechet <= faktNachKmPantographNechet + 2200 && distanceNechet >= faktNachKmPantographNechet + 2101){
                                faktNachKmPantograph2200 = faktNachKmPantographNechet + 2200
                                faktNachKmPantograph2101 = faktNachKmPantographNechet + 2101
                            }

                            if (distanceNechet <= faktNachKmPantographNechet + 2100 && distanceNechet >= faktNachKmPantographNechet + 2001){
                                faktNachKmPantograph2100 = faktNachKmPantographNechet + 2100
                                faktNachKmPantograph2001 = faktNachKmPantographNechet + 2001
                            }

                            // Конец оповещения опускания токоприемников
                        }
//                        myDbManagerPantograph.closeDb()
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        myDbManagerBrake.openDb()
                        val dataListBrakes = myDbManagerBrake.readDbDataBrakeNechet()
                        for (item in dataListBrakes){

                            titleStartBrakeNechet = item.startNechet
                            piketStartBrakeNechet = item.picketStartNechet

                            // Начало расчёта начала Торможения по киллометро

                            var x = 9999999
                            var kmx = 10000
                            val pkx = 10
                            while (x > 0){
                                x -= 1000
                                kmx -= 1

                                if (titleStartBrakeNechet == kmx && piketStartBrakeNechet == pkx){
                                    faktNachKmBrakeNechet = x
                                    piketNachKmBrakeNechet = pkx
                                }
                            }

                            var x1 = 9999899
                            var kmx1 = 10000
                            val pkx1 = 9
                            while (x1 > 0){
                                x1 -= 1000
                                kmx1 -= 1

                                if (titleStartBrakeNechet == kmx1 && piketStartBrakeNechet == pkx1){
                                    faktNachKmBrakeNechet = x1
                                    piketNachKmBrakeNechet = pkx1
                                }
                            }

                            var x2 = 9999799
                            var kmx2 = 10000
                            val pkx2 = 8
                            while (x2 > 0){
                                x2 -= 1000
                                kmx2 -= 1

                                if (titleStartBrakeNechet == kmx2 && piketStartBrakeNechet == pkx2){
                                    faktNachKmBrakeNechet = x2
                                    piketNachKmBrakeNechet = pkx2
                                }
                            }

                            var x3 = 9999699
                            var kmx3 = 10000
                            val pkx3 = 7
                            while (x3 > 0){
                                x3 -= 1000
                                kmx3 -= 1

                                if (titleStartBrakeNechet == kmx3 && piketStartBrakeNechet == pkx3){
                                    faktNachKmBrakeNechet = x3
                                    piketNachKmBrakeNechet = pkx3
                                }
                            }

                            var x4 = 9999599
                            var kmx4 = 10000
                            val pkx4 = 6
                            while (x4 > 0){
                                x4 -= 1000
                                kmx4 -= 1

                                if (titleStartBrakeNechet == kmx4 && piketStartBrakeNechet == pkx4){
                                    faktNachKmBrakeNechet = x4
                                    piketNachKmBrakeNechet = pkx4
                                }
                            }

                            var x5 = 9999499
                            var kmx5 = 10000
                            val pkx5 = 5
                            while (x5 > 0){
                                x5 -= 1000
                                kmx5 -= 1

                                if (titleStartBrakeNechet == kmx5 && piketStartBrakeNechet == pkx5){
                                    faktNachKmBrakeNechet = x5
                                    piketNachKmBrakeNechet = pkx5
                                }
                            }

                            var x6 = 9999399
                            var kmx6 = 10000
                            val pkx6 = 4
                            while (x6 > 0){
                                x6 -= 1000
                                kmx6 -= 1

                                if (titleStartBrakeNechet == kmx6 && piketStartBrakeNechet == pkx6){
                                    faktNachKmBrakeNechet = x6
                                    piketNachKmBrakeNechet = pkx6
                                }
                            }

                            var x7 = 9999299
                            var kmx7 = 10000
                            val pkx7 = 3
                            while (x7 > 0){
                                x7 -= 1000
                                kmx7 -= 1

                                if (titleStartBrakeNechet == kmx7 && piketStartBrakeNechet == pkx7){
                                    faktNachKmBrakeNechet = x7
                                    piketNachKmBrakeNechet = pkx7
                                }
                            }

                            var x8 = 9999199
                            var kmx8 = 10000
                            val pkx8 = 2
                            while (x8 > 0){
                                x8 -= 1000
                                kmx8 -= 1

                                if (titleStartBrakeNechet == kmx8 && piketStartBrakeNechet == pkx8){
                                    faktNachKmBrakeNechet = x8
                                    piketNachKmBrakeNechet = pkx8
                                }
                            }

                            var x9 = 9999099
                            var kmx9 = 10000
                            val pkx9 = 1
                            while (x9 > 0){
                                x9 -= 1000
                                kmx9 -= 1

                                if (titleStartBrakeNechet == kmx9 && piketStartBrakeNechet == pkx9){
                                    faktNachKmBrakeNechet = x9
                                    piketNachKmBrakeNechet = pkx9
                                }
                            }

                            // Конец расчёта начала Торможения по киллометро

                            // Начало оповещения Торможения

                            if (distanceNechet <= faktNachKmBrakeNechet + 4000 && distanceNechet >= faktNachKmBrakeNechet + 3901){
                                faktNachKmBrake4000 = faktNachKmBrakeNechet + 4000
                                faktNachKmBrake3901 = faktNachKmBrakeNechet + 3901
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 3900 && distanceNechet >= faktNachKmBrakeNechet + 3801){
                                faktNachKmBrake3900 = faktNachKmBrakeNechet + 3900
                                faktNachKmBrake3801 = faktNachKmBrakeNechet + 3801
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 3800 && distanceNechet >= faktNachKmBrakeNechet + 3701){
                                faktNachKmBrake3800 = faktNachKmBrakeNechet + 3800
                                faktNachKmBrake3701 = faktNachKmBrakeNechet + 3701
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 3700 && distanceNechet >= faktNachKmBrakeNechet + 3601){
                                faktNachKmBrake3700 = faktNachKmBrakeNechet + 3700
                                faktNachKmBrake3601 = faktNachKmBrakeNechet + 3601
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 3600 && distanceNechet >= faktNachKmBrakeNechet + 3501){
                                faktNachKmBrake3600 = faktNachKmBrakeNechet + 3600
                                faktNachKmBrake3501 = faktNachKmBrakeNechet + 3501
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 2900 && distanceNechet >= faktNachKmBrakeNechet + 2801){
                                faktNachKmBrake2900 = faktNachKmBrakeNechet + 2900
                                faktNachKmBrake2801 = faktNachKmBrakeNechet + 2801
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 2800 && distanceNechet >= faktNachKmBrakeNechet + 2701){
                                faktNachKmBrake2800 = faktNachKmBrakeNechet + 2800
                                faktNachKmBrake2701 = faktNachKmBrakeNechet + 2701
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 2700 && distanceNechet >= faktNachKmBrakeNechet + 2601){
                                faktNachKmBrake2700 = faktNachKmBrakeNechet + 2700
                                faktNachKmBrake2601 = faktNachKmBrakeNechet + 2601
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 2600 && distanceNechet >= faktNachKmBrakeNechet + 2501){
                                faktNachKmBrake2600 = faktNachKmBrakeNechet + 2600
                                faktNachKmBrake2501 = faktNachKmBrakeNechet + 2501
                            }

                            if (distanceNechet <= faktNachKmBrakeNechet + 2500 && distanceNechet >= faktNachKmBrakeNechet + 2401){
                                faktNachKmBrake2500 = faktNachKmBrakeNechet + 2500
                                faktNachKmBrake2401 = faktNachKmBrakeNechet + 2401
                            }

                            // Конец оповещения Торможения
                        }
//                        myDbManagerBrake.closeDb()
                    }

                    CoroutineScope(Dispatchers.Main).launch {
//                        startIfinishLimitationsKm()
                        myDbManagerLimitations.openDb()
                        val dataListLimitations = myDbManagerLimitations.readDbDataLimitationsNechet()
                        sumCalculateUslNechet = (uslNechet * 14) + 50
                        for (item in dataListLimitations){

                            titleStartNechet = item.startNechet
                            piketStartNechet = item.picketStartNechet
                            titleFinishNechet = item.finishNechet
                            piketFinishNechet = item.picketFinishNechet
                            speedNechet = item.speedNechet

                            // Расчёт начала ограничения по киллометро

                            var x = 9999999
                            var kmx = 10000
                            val pkx = 10
                            while (x > 0){
                                x -= 1000
                                kmx -= 1

                                if (titleStartNechet == kmx && piketStartNechet == pkx){
                                    faktNachKmNechet = x
                                    piketNachKmNechet = pkx
                                }
                            }

                            var x1 = 9999899
                            var kmx1 = 10000
                            val pkx1 = 9
                            while (x1 > 0){
                                x1 -= 1000
                                kmx1 -= 1

                                if (titleStartNechet == kmx1 && piketStartNechet == pkx1){
                                    faktNachKmNechet = x1
                                    piketNachKmNechet = pkx1
                                }
                            }

                            var x2 = 9999799
                            var kmx2 = 10000
                            val pkx2 = 8
                            while (x2 > 0){
                                x2 -= 1000
                                kmx2 -= 1

                                if (titleStartNechet == kmx2 && piketStartNechet == pkx2){
                                    faktNachKmNechet = x2
                                    piketNachKmNechet = pkx2
                                }
                            }

                            var x3 = 9999699
                            var kmx3 = 10000
                            val pkx3 = 7
                            while (x3 > 0){
                                x3 -= 1000
                                kmx3 -= 1

                                if (titleStartNechet == kmx3 && piketStartNechet == pkx3){
                                    faktNachKmNechet = x3
                                    piketNachKmNechet = pkx3
                                }
                            }

                            var x4 = 9999599
                            var kmx4 = 10000
                            val pkx4 = 6
                            while (x4 > 0){
                                x4 -= 1000
                                kmx4 -= 1

                                if (titleStartNechet == kmx4 && piketStartNechet == pkx4){
                                    faktNachKmNechet = x4
                                    piketNachKmNechet = pkx4
                                }
                            }

                            var x5 = 9999499
                            var kmx5 = 10000
                            val pkx5 = 5
                            while (x5 > 0){
                                x5 -= 1000
                                kmx5 -= 1

                                if (titleStartNechet == kmx5 && piketStartNechet == pkx5){
                                    faktNachKmNechet = x5
                                    piketNachKmNechet = pkx5
                                }
                            }

                            var x6 = 9999399
                            var kmx6 = 10000
                            val pkx6 = 4
                            while (x6 > 0){
                                x6 -= 1000
                                kmx6 -= 1

                                if (titleStartNechet == kmx6 && piketStartNechet == pkx6){
                                    faktNachKmNechet = x6
                                    piketNachKmNechet = pkx6
                                }
                            }

                            var x7 = 9999299
                            var kmx7 = 10000
                            val pkx7 = 3
                            while (x7 > 0){
                                x7 -= 1000
                                kmx7 -= 1

                                if (titleStartNechet == kmx7 && piketStartNechet == pkx7){
                                    faktNachKmNechet = x7
                                    piketNachKmNechet = pkx7
                                }
                            }

                            var x8 = 9999199
                            var kmx8 = 10000
                            val pkx8 = 2
                            while (x8 > 0){
                                x8 -= 1000
                                kmx8 -= 1

                                if (titleStartNechet == kmx8 && piketStartNechet == pkx8){
                                    faktNachKmNechet = x8
                                    piketNachKmNechet = pkx8
                                }
                            }

                            var x9 = 9999099
                            var kmx9 = 10000
                            val pkx9 = 1
                            while (x9 > 0){
                                x9 -= 1000
                                kmx9 -= 1

                                if (titleStartNechet == kmx9 && piketStartNechet == pkx9){
                                    faktNachKmNechet = x9
                                    piketNachKmNechet = pkx9
                                }
                            }

                            // Конец расчёта начала ограничения по киллометро

                            // Расчёт конца ограничения по киллометро

                            var z = 9999901
                            var kms = 10000
                            val pkc = 10

                            while (z > 0){
                                z -= 1000
                                kms -= 1

                                if (titleFinishNechet == kms && piketFinishNechet == pkc){
                                    faktEndKmNechet = z
                                    piketEndKmNechet = pkc
                                }
                            }

                            var z1 = 9999801
                            var kms1 = 10000
                            val pkc1 = 9

                            while (z1 > 0){
                                z1 -= 1000
                                kms1 -= 1

                                if (titleFinishNechet == kms1 && piketFinishNechet == pkc1){
                                    faktEndKmNechet = z1
                                    piketEndKmNechet = pkc1
                                }
                            }

                            var z2 = 9999701
                            var kms2 = 10000
                            val pkc2 = 8

                            while (z2 > 0){
                                z2 -= 1000
                                kms2 -= 1

                                if (titleFinishNechet == kms2 && piketFinishNechet == pkc2){
                                    faktEndKmNechet = z2
                                    piketEndKmNechet = pkc2
                                }
                            }

                            var z3 = 9999601
                            var kms3 = 10000
                            val pkc3 = 7

                            while (z3 > 0){
                                z3 -= 1000
                                kms3 -= 1

                                if (titleFinishNechet == kms3 && piketFinishNechet == pkc3){
                                    faktEndKmNechet = z3
                                    piketEndKmNechet = pkc3
                                }
                            }

                            var z4 = 9999501
                            var kms4 = 10000
                            val pkc4 = 6

                            while (z4 > 0){
                                z4 -= 1000
                                kms4 -= 1

                                if (titleFinishNechet == kms4 && piketFinishNechet == pkc4){
                                    faktEndKmNechet = z4
                                    piketEndKmNechet = pkc4
                                }
                            }

                            var z5 = 9999401
                            var kms5 = 10000
                            val pkc5 = 5

                            while (z5 > 0){
                                z5 -= 1000
                                kms5 -= 1

                                if (titleFinishNechet == kms5 && piketFinishNechet == pkc5){
                                    faktEndKmNechet = z5
                                    piketEndKmNechet = pkc5
                                }
                            }

                            var z6 = 9999301
                            var kms6 = 10000
                            val pkc6 = 4

                            while (z6 > 0){
                                z6 -= 1000
                                kms6 -= 1

                                if (titleFinishNechet == kms6 && piketFinishNechet == pkc6){
                                    faktEndKmNechet = z6
                                    piketEndKmNechet = pkc6
                                }
                            }

                            var z7 = 9999201
                            var kms7 = 10000
                            val pkc7 = 3

                            while (z7 > 0){
                                z7 -= 1000
                                kms7 -= 1

                                if (titleFinishNechet == kms7 && piketFinishNechet == pkc7){
                                    faktEndKmNechet = z7
                                    piketEndKmNechet = pkc7
                                }
                            }

                            var z8 = 9999101
                            var kms8 = 10000
                            val pkc8 = 2

                            while (z8 > 0){
                                z8 -= 1000
                                kms8 -= 1

                                if (titleFinishNechet == kms8 && piketFinishNechet == pkc8){
                                    faktEndKmNechet = z8
                                    piketEndKmNechet = pkc8
                                }
                            }

                            var z9 = 9999001
                            var kms9 = 10000
                            val pkc9 = 1

                            while (z9 > 0){
                                z9 -= 1000
                                kms9 -= 1

                                if (titleFinishNechet == kms9 && piketFinishNechet == pkc9){
                                    faktEndKmNechet = z9
                                    piketEndKmNechet = pkc9
                                }
                            }

                            // Конец расчёта конца ограничения по киллометро

                            // Оповещение выключения и включения саут

                            if (distanceNechet >= 6955900 + 1 && distanceNechet <= 6955948 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(sautout)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6955800 + 1 && distanceNechet <= 6955848 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(sautout)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6955700 + 1 && distanceNechet <= 6955748 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(sautout)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6955600 + 1 && distanceNechet <= 6955648 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(sautout)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6955500 + 1 && distanceNechet <= 6955548 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(sautout)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6955400 + 1 && distanceNechet <= 6955448 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(sautout)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6955350 + 1 && distanceNechet <= 6955398 + 1){
                                isSautNechet2 = true
                            }

                            if (distanceNechet >= 6907900 + 1 && distanceNechet <= 6907948 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(saut)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6907800 + 1 && distanceNechet <= 6907848 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(saut)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6907700 + 1 && distanceNechet <= 6907748 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(saut)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6907600 + 1 && distanceNechet <= 6907648 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(saut)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6907500 + 1 && distanceNechet <= 6907548 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(saut)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6907400 + 1 && distanceNechet <= 6907448 + 1 && isSautNechet15 && isSautNechet25 && isSautNechet40 && isSautNechet50
                                && isSautNechet55 && isSautNechet60 && isSautNechet65 && isSautNechet70 && isSautNechet75 && isSautNechet2){
                                playSound(saut)
                                isSautNechet2 = !isSautNechet2
                            }
                            if (distanceNechet >= 6907350 + 1 && distanceNechet <= 6907398 + 1){
                                isSautNechet2 = true
                            }

                            // Начало оповещения ограничения скорости за 2 км/ч

                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet + 1901){
                                faktNachKm2000 = faktNachKmNechet + 2000
                                faktNachKm1901 = faktNachKmNechet + 1901

                                val arrayListSpeed = mutableListOf(int15, int25, int40, int50, int55, int60, int65, int70, int75)

                                if (item.speedNechet == 15){
                                    int15 = item.speedNechet
                                    arrayListSpeed.add(int15)
                                }
                                if (item.speedNechet == 25){
                                    int25 = item.speedNechet
                                    arrayListSpeed.add(int25)
                                }
                                if (item.speedNechet == 40){
                                    int40 = item.speedNechet
                                    arrayListSpeed.add(int40)
                                }
                                if (item.speedNechet == 50){
                                    int50 = item.speedNechet
                                    arrayListSpeed.add(int50)
                                }
                                if (item.speedNechet == 55){
                                    int55 = item.speedNechet
                                    arrayListSpeed.add(int55)
                                }
                                if (item.speedNechet == 60){
                                    int60 = item.speedNechet
                                    arrayListSpeed.add(int60)
                                }
                                if (item.speedNechet == 65){
                                    int65 = item.speedNechet
                                    arrayListSpeed.add(int65)
                                }
                                if (item.speedNechet == 70){
                                    int70 = item.speedNechet
                                    arrayListSpeed.add(int70)
                                }
                                if (item.speedNechet == 75){
                                    int75 = item.speedNechet
                                    arrayListSpeed.add(int75)
                                }

                                Log.d("MyLog", "minSpeed = ${arrayListSpeed.min()}")
                                speedNechetMin = arrayListSpeed.min()
                            }

                            if (distanceNechet <= faktNachKmNechet + 1900 && distanceNechet >= faktNachKmNechet + 1801) {
                                faktNachKm1900 = faktNachKmNechet + 1900
                                faktNachKm1801 = faktNachKmNechet + 1801
                            }
                            if (distanceNechet >= faktNachKmNechet + 1800 && distanceNechet >= faktNachKmNechet + 1701) {
                                faktNachKm1800 = faktNachKmNechet + 1800
                                faktNachKm1701 = faktNachKmNechet + 1701
                            }
                            if (distanceNechet <= faktNachKmNechet + 1700 && distanceNechet >= faktNachKmNechet + 1601) {
                                faktNachKm1700 = faktNachKmNechet + 1700
                                faktNachKm1601 = faktNachKmNechet + 1601
                            }
                            if (distanceNechet <= faktNachKmNechet + 1600 && distanceNechet >= faktNachKmNechet + 1501) {
                                faktNachKm1600 = faktNachKmNechet + 1600
                                faktNachKm1501 = faktNachKmNechet + 1501
                            }

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 15){
                                int15 = 150
                                isLimitationsNechet15 = true
                                isSautNechet15 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 25){
                                int25 = 150
                                isLimitationsNechet25 = true
                                isSautNechet25 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 40){
                                int40 = 150
                                isLimitationsNechet40 = true
                                isSautNechet40 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 50){
                                int50 = 150
                                isLimitationsNechet50 = true
                                isSautNechet50 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 55){
                                int55 = 150
                                isLimitationsNechet55 = true
                                isSautNechet55 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 60){
                                int60 = 150
                                isLimitationsNechet60 = true
                                isSautNechet60 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 65){
                                int65 = 150
                                isLimitationsNechet65 = true
                                isSautNechet65 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 70){
                                int70 = 150
                                isLimitationsNechet70 = true
                                isSautNechet70 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet + 1899 && distanceNechet >= faktNachKmNechet + 1851 && item.speedNechet == 75){
                                int75 = 150
                                isLimitationsNechet75 = true
                                isSautNechet75 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            // Конец оповещения ограничения скорости за 2 км/ч

                            // Предупреждение о превышении за 400 метров до предупреждения

                            if (currentLocationNechet.speed * 3.6 >= 15 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 15 && isLimitationsChet400m15){
                                playSound(voiceprev)
                                isLimitationsChet400m15 = !isLimitationsChet400m15
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 15){
                                isLimitationsChet400m15 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 25 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 25 && isLimitationsChet400m25){
                                playSound(voiceprev)
                                isLimitationsChet400m25 = !isLimitationsChet400m25
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 25){
                                isLimitationsChet400m25 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 40 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 40 && isLimitationsChet400m40){
                                playSound(voiceprev)
                                isLimitationsChet400m40 = !isLimitationsChet400m40
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 40){
                                isLimitationsChet400m40 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 50 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 50 && isLimitationsChet400m50){
                                playSound(voiceprev)
                                isLimitationsChet400m50 = !isLimitationsChet400m50
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet.toInt() == 50){
                                isLimitationsChet400m50 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 55 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 55 && isLimitationsChet400m55){
                                playSound(voiceprev)
                                isLimitationsChet400m55 = !isLimitationsChet400m55
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 55){
                                isLimitationsChet400m55 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 60 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 60 && isLimitationsChet400m60){
                                playSound(voiceprev)
                                isLimitationsChet400m60 = !isLimitationsChet400m60
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 60){
                                isLimitationsChet400m60 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 65 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 65 && isLimitationsChet400m65){
                                playSound(voiceprev)
                                isLimitationsChet400m65 = !isLimitationsChet400m65
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 65){
                                isLimitationsChet400m65 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 70 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 70 && isLimitationsChet400m70){
                                playSound(voiceprev)
                                isLimitationsChet400m70 = !isLimitationsChet400m70
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 70){
                                isLimitationsChet400m70 = true
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (currentLocationNechet.speed * 3.6 >= 75 && distanceNechet <= faktNachKmNechet + 549 && distanceNechet >= faktNachKmNechet + 501 && item.speedNechet == 75 && isLimitationsChet400m75){
                                playSound(voiceprev)
                                isLimitationsChet400m75 = !isLimitationsChet400m75
                            }
                            if (distanceNechet <= faktNachKmNechet + 499 && distanceNechet >= faktNachKmNechet + 451 && item.speedNechet == 75){
                                isLimitationsChet400m75 = true
                            }

                            // Конец предупреждения о превышении за 400 метров до предупреждения

                            // Предупреждение о превышении ограничения скорости

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 15){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 13 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 15 && isOgr){
                                    playSound(ogr15)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 15){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 15){
                                    val nachKm15 = faktNachKmNechet
                                    val endKm15 = faktEndKmNechet
                                    val speed15 = item.speedNechet
                                    if (distanceNechet <= nachKm15 + 2000 && distanceNechet >= endKm15 - sumCalculateUslNechet && speed15 == 15){
                                        tvOgrNechet15 = "$speedNechet"
                                        tvKmPkNechet15 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 15){
                                tvOgrNechet15 = ""
                                tvKmPkNechet15 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 25){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 23 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 25 && isOgr){
                                    playSound(ogr25)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 25){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 25){
                                    val nachKm25 = faktNachKmNechet
                                    val endKm25 = faktEndKmNechet
                                    val speed25 = item.speedNechet
                                    if (distanceNechet <= nachKm25 + 2000 && distanceNechet >= endKm25 - sumCalculateUslNechet && speed25 == 25){
                                        tvOgrNechet25 = "$speedNechet"
                                        tvKmPkNechet25 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 25){
                                tvOgrNechet25 = ""
                                tvKmPkNechet25 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 40){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 38 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 40 && isOgr){
                                    playSound(ogr40)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 40){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 40){
                                    val nachKm40 = faktNachKmNechet
                                    val endKm40 = faktEndKmNechet
                                    val speed40 = item.speedNechet
                                    if (distanceNechet <= nachKm40 + 2000 && distanceNechet >= endKm40 - sumCalculateUslNechet && speed40 == 40){
                                        tvOgrNechet40 = "$speedNechet"
                                        tvKmPkNechet40 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 40){
                                tvOgrNechet40 = ""
                                tvKmPkNechet40 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 50){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 48 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 50 && isOgr){
                                    playSound(ogr50)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 50){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 50){
                                    val nachKm50 = faktNachKmNechet
                                    val endKm50 = faktEndKmNechet
                                    val speed50 = item.speedNechet
                                    if (distanceNechet <= nachKm50 + 2000 && distanceNechet >= endKm50 - sumCalculateUslNechet && speed50 == 50){
                                        tvOgrNechet50 = "$speedNechet"
                                        tvKmPkNechet50 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 50){
                                tvOgrNechet50 = ""
                                tvKmPkNechet50 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 55){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 53 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 55 && isOgr){
                                    playSound(ogr55)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 55){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 55){
                                    val nachKm55 = faktNachKmNechet
                                    val endKm55 = faktEndKmNechet
                                    val speed55 = item.speedNechet
                                    if (distanceNechet <= nachKm55 + 2000 && distanceNechet >= endKm55 - sumCalculateUslNechet && speed55 == 55){
                                        tvOgrNechet55 = "$speedNechet"
                                        tvKmPkNechet55 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 55){
                                tvOgrNechet55 = ""
                                tvKmPkNechet55 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 60){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 58 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 60 && isOgr){
                                    playSound(ogr60)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 60){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 60){
                                    val nachKm60 = faktNachKmNechet
                                    val endKm60 = faktEndKmNechet
                                    val speed60 = item.speedNechet
                                    if (distanceNechet <= nachKm60 + 2000 && distanceNechet >= endKm60 - sumCalculateUslNechet && speed60 == 60){
                                        tvOgrNechet60 = "$speedNechet"
                                        tvKmPkNechet60 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 60){
                                tvOgrNechet60 = ""
                                tvKmPkNechet60 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 65){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 63 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 65 && isOgr){
                                    playSound(ogr65)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 65){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 65){
                                    val nachKm65 = faktNachKmNechet
                                    val endKm65 = faktEndKmNechet
                                    val speed65 = item.speedNechet
                                    if (distanceNechet <= nachKm65 + 2000 && distanceNechet >= endKm65 - sumCalculateUslNechet && speed65 == 65){
                                        tvOgrNechet65 = "$speedNechet"
                                        tvKmPkNechet65 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 65){
                                tvOgrNechet65 = ""
                                tvKmPkNechet65 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 70){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 68 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 70 && isOgr){
                                    playSound(ogr70)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 70){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 70){
                                    val nachKm70 = faktNachKmNechet
                                    val endKm70 = faktEndKmNechet
                                    val speed70 = item.speedNechet
                                    if (distanceNechet <= nachKm70 + 2000 && distanceNechet >= endKm70 - sumCalculateUslNechet && speed70 == 70){
                                        tvOgrNechet70 = "$speedNechet"
                                        tvKmPkNechet70 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 70){
                                tvOgrNechet70 = ""
                                tvKmPkNechet70 = ""
                            }

                            //---------------------------------------------------------------------------------------------------------------------------------------

                            if (distanceNechet <= faktNachKmNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 75){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (currentLocationNechet.speed * 3.6 >= 73 && distanceNechet <= faktNachKmNechet && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 75 && isOgr){
                                    playSound(ogr75)
                                    isOgr = false
                                } else {
                                    isOgr = true
                                }
                            }
                            if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet && item.speedNechet == 75){
                                val sumDistance = (faktNachKmNechet - faktEndKmNechet) + sumCalculateUslNechet
                                if (distanceNechet <= faktNachKmNechet + 2000 && distanceNechet >= faktNachKmNechet - sumDistance && item.speedNechet == 75){
                                    val nachKm75 = faktNachKmNechet
                                    val endKm75 = faktEndKmNechet
                                    val speed75 = item.speedNechet
                                    if (distanceNechet <= nachKm75 + 2000 && distanceNechet >= endKm75 - sumCalculateUslNechet && speed75 == 75){
                                        tvOgrNechet75 = "$speedNechet"
                                        tvKmPkNechet75 = "$titleStartNechet км $piketStartNechet пк - $titleFinishNechet км $piketFinishNechet пк"
                                    }
                                }
                            }
                            if (distanceNechet <= faktEndKmNechet - sumCalculateUslNechet && distanceNechet >= faktEndKmNechet - sumCalculateUslNechet - 50 && item.speedNechet == 75){
                                tvOgrNechet75 = ""
                                tvKmPkNechet75 = ""
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
                val locModelNechetFakt = LocationModelNechetFakt(
                    currentLocationNechet.speed,
                    distanceNechet,
                    kmDistanceNechetFakt,
                    pkDistanceNechetFakt,
                    tvOgrNechet15,
                    tvOgrNechet25,
                    tvOgrNechet40,
                    tvOgrNechet50,
                    tvOgrNechet55,
                    tvOgrNechet60,
                    tvOgrNechet65,
                    tvOgrNechet70,
                    tvOgrNechet75,
                    tvKmPkNechet15,
                    tvKmPkNechet25,
                    tvKmPkNechet40,
                    tvKmPkNechet50,
                    tvKmPkNechet55,
                    tvKmPkNechet60,
                    tvKmPkNechet65,
                    tvKmPkNechet70,
                    tvKmPkNechet75
                )
                sendLocDataNechetFakt(locModelNechetFakt)
            }
            lastLocationNechetFakt = currentLocationNechet
        }
    }

    private suspend fun calculationKmFakt() = withContext(Dispatchers.IO){
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
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w9
                faktFinishKmNechet = q9
            }

            if (distanceNechet <= w8 && distanceNechet >= q8){
                pke = 9
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w8
                faktFinishKmNechet = q8
            }

            if (distanceNechet <= w7 && distanceNechet >= q7){
                pke = 8
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w7
                faktFinishKmNechet = q7
            }

            if (distanceNechet <= w6 && distanceNechet >= q6){
                pke = 7
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w6
                faktFinishKmNechet = q6
            }

            if (distanceNechet <= w5 && distanceNechet >= q5){
                pke = 6
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w5
                faktFinishKmNechet = q5
            }

            if (distanceNechet <= w4 && distanceNechet >= q4){
                pke = 5
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w4
                faktFinishKmNechet = q4
            }

            if (distanceNechet <= w3 && distanceNechet >= q3){
                pke = 4
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w3
                faktFinishKmNechet = q3
            }

            if (distanceNechet <= w2 && distanceNechet >= q2){
                pke = 3
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w2
                faktFinishKmNechet = q2
            }

            if (distanceNechet <= w1 && distanceNechet >= q1){
                pke = 2
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w1
                faktFinishKmNechet = q1
            }

            if (distanceNechet <= w && distanceNechet >= q){
                pke = 1
                kmDistanceNechetFakt = kme
                pkDistanceNechetFakt = pke

                faktStartKmNechet = w
                faktFinishKmNechet = q
            }
        }
    }

    private fun sendLocDataNechetFakt(locModelNechetFakt: LocationModelNechetFakt){
        val iNechetFakt = Intent(LOC_MODEL_INTENT_NECHET_FAKT)
        iNechetFakt.putExtra(LOC_MODEL_INTENT_NECHET_FAKT, locModelNechetFakt)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(iNechetFakt)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun startNotificationNechetFakt(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val nChannelNechetFakt = NotificationChannel(
                CHANNEL_ID_NECHET_FAKT,
                "Location Service Fakt",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManagerNechetFakt = getSystemService(NotificationManager::class.java) as NotificationManager
            nManagerNechetFakt.createNotificationChannel(nChannelNechetFakt)
        }
        val nIntentNechetFakt = Intent(this, ActivityNechet::class.java)
        val pIntentNechetFakt = PendingIntent.getActivity(
            this,
            22,
            nIntentNechetFakt,
            PendingIntent.FLAG_MUTABLE
        )
        val notificationNechetFakt = NotificationCompat.Builder(
            this,
            CHANNEL_ID_NECHET_FAKT
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("GPS assistant Нечётный запущен!")
            .setContentIntent(pIntentNechetFakt).build()
        startForeground(92, notificationNechetFakt)
    }

    private fun initLocationNechetFakt(){
        locRequestNechetFakt = LocationRequest.create()
        locRequestNechetFakt.interval = 1000
        locRequestNechetFakt.fastestInterval = 1000
        locRequestNechetFakt.priority = PRIORITY_HIGH_ACCURACY
        locProviderNechetFakt = LocationServices.getFusedLocationProviderClient(baseContext)

    }

    private fun startLocationUpdatesNechetFakt(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED

        ) return

        locProviderNechetFakt.requestLocationUpdates(
            locRequestNechetFakt,
            locCallbackNechetFakt,
            Looper.myLooper()
        )
    }

    companion object{
        const val LOC_MODEL_INTENT_NECHET_FAKT = "loc_intent_nechet_fakt"
        const val CHANNEL_ID_NECHET_FAKT = "channel_nechet_fakt"
        var isRunningNechetFakt = false
    }
}