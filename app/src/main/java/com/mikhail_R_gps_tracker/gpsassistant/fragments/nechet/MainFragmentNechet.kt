package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mikhail_R_gps_tracker.gpsassistant.MainApp
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentMainNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItem
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItemNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.MainFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentMinusChet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentMinusNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentModelNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentModelUslNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentPlusChet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentPlusNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelNechetFakt
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceChet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceChetFakt
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceNechetFakt
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewChet
//import com.mikhail_R_gps_tracker.gpsassistent.location.LocationServiceNechet.Companion.startTimeNechet
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewNechet
import com.mikhail_R_gps_tracker.gpsassistant.utils.DialogManagerNechet
import com.mikhail_R_gps_tracker.gpsassistant.utils.checkPermissionNechet
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast
import com.yandex.mobile.ads.impl.fi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.StringBuilder

@Suppress("DEPRECATION", "UNREACHABLE_CODE")
class MainFragmentNechet : Fragment() {
    private var sbL = StringBuilder("")
    private var pl: Polyline? = null
    private lateinit var tvDistancePrimerNechet: TextView
    private lateinit var tvDistanceNechet: TextView
    private var mainDistanceNechet = 0.0f
    private var mainUslNechet = 0.0f
    private var savePlus50 = 0.0f
    private var saveMinus50 = 0.0f

    private var locationModelNechet: LocationModelNechet? = null
    private var locationModelNechetFakt: LocationModelNechetFakt? = null
    private var isServiceRunningNechet = false
    private var isServiceRunningNechetFakt = false
    private var firstStart = true
//    private var timerNechet: Timer? = null
//    private var startTimeNechet = 0L
    private lateinit var mLocOverlayNechet: MyLocationNewOverlay
    private lateinit var pLauncherNechet: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMainNechetBinding
    private val modelNechet: ModelViewNechet by activityViewModels{
        ModelViewNechet.ViewModelFactory((requireContext().applicationContext as MainApp).databaseNechet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        CoroutineScope(Dispatchers.IO).launch { settingsOsmNechet() }
        binding = FragmentMainNechetBinding.inflate(inflater, container, false)
        return binding.root

        tvDistancePrimerNechet = tvDistancePrimerNechet
        tvDistanceNechet = tvDistanceNechet
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissionsNechet()
        setOnClicksNechet()
        checkServiceStateNechet()
        checkServiceStateNechetFakt()
//        CoroutineScope(Dispatchers.IO).launch { updateTimeNechet() }
        registerLocReceiverNechet()
        registerLocReceiverNechetFakt()
        locationUpdateNechetFakt()
        locationUpdateNechet()
    }

    private fun setOnClicksNechet() = with(binding){
        val listenerNechet = onClicksNechet()
        fStartStopNechet.setOnClickListener(listenerNechet)
        fStartNechetFakt.setOnClickListener(listenerNechet)
        fCenterNechet.setOnClickListener(listenerNechet)
        mainDistanceNechet.setOnClickListener(listenerNechet)
        mainUslNechet.setOnClickListener(listenerNechet)
        savePlusNechet50.setOnClickListener(listenerNechet)
        saveMinusNechet50.setOnClickListener(listenerNechet)
    }

    private fun onClicksNechet(): View.OnClickListener {
        return View.OnClickListener {
            when(it.id){
                R.id.fStartStopNechet -> CoroutineScope(Dispatchers.IO).launch { startStopServiceNechet() }
                R.id.fStartNechetFakt -> CoroutineScope(Dispatchers.IO).launch { startStopServiceNechetFakt() }
                R.id.fCenterNechet -> centerLocationNechet()
                R.id.mainDistanceNechet -> CoroutineScope(Dispatchers.IO).launch { mainDistanceNechet() }
                R.id.mainUslNechet -> CoroutineScope(Dispatchers.IO).launch { mainUslNechet() }
                R.id.savePlusNechet50 -> CoroutineScope(Dispatchers.IO).launch { saveMinus() }
                R.id.saveMinusNechet50 -> CoroutineScope(Dispatchers.IO).launch { savePlus() }
            }
        }
    }

    private suspend fun saveMinus() = withContext(Dispatchers.Main) {
        saveMinus50 = 20.0f
        val fragmentMinusNechet = FragmentMinusNechet(
            saveMinus50
        )
        sendFragmentMinusNechet(fragmentMinusNechet)
    }

    private fun sendFragmentMinusNechet(fragmentMinusNechet: FragmentMinusNechet){
        val intentNechet = Intent(LOC_MODEL_INTENT_FRAGMENT_MINUS_NECHET)
        intentNechet.putExtra(LOC_MODEL_INTENT_FRAGMENT_MINUS_NECHET, fragmentMinusNechet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentNechet) }
    }

    private suspend fun savePlus() = withContext(Dispatchers.Main) {
        savePlus50 = 20.0f
        val fragmentPlusNechet = FragmentPlusNechet(
            savePlus50
        )
        sendFragmentPlusNechet(fragmentPlusNechet)
    }

    private fun sendFragmentPlusNechet(fragmentPlusNechet: FragmentPlusNechet){
        val intentNechet = Intent(LOC_MODEL_INTENT_FRAGMENT_PLUS_NECHET)
        intentNechet.putExtra(LOC_MODEL_INTENT_FRAGMENT_PLUS_NECHET, fragmentPlusNechet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentNechet) }
    }

    private suspend fun mainDistanceNechet() = withContext(Dispatchers.Main){

        DialogManagerNechet.showDialogMainDistanceNechet(requireContext(),
            object : DialogManagerNechet.ListenerMainDistanceNechet{
                override fun onClickMainDistanceNechet(dialogDistanceNechet: String) {
                    if (dialogDistanceNechet == ""){
                        showToast("Вы не ввели значение")
                    }else{

                        mainDistanceNechet = dialogDistanceNechet.toFloat()

                        if (binding.saveMinusNechet50.isGone){
                            binding.saveMinusNechet50.isGone = binding.saveMinusNechet50.isVisible
                        }
                        if (binding.savePlusNechet50.isGone){
                            binding.savePlusNechet50.isGone = binding.savePlusNechet50.isVisible
                        }
                        if (binding.tvDistanceNechet.isGone){
                            binding.tvDistanceNechet.isGone = binding.tvDistanceNechet.isVisible
                        }
                        if (binding.tvSpeedNechet.isGone){
                            binding.tvSpeedNechet.isGone = binding.tvSpeedNechet.isVisible
                        }

                        val fragmentNechet = FragmentModelNechet(
                            mainDistanceNechet,
                        )
                        sendFragmentNechet(fragmentNechet)
                        showToast("Сохранено")
                    }
                }
            })
    }

    private fun sendFragmentNechet(fragmentNechet: FragmentModelNechet){
        val intentNechet = Intent(LOC_MODEL_INTENT_FRAGMENT_NECHET)
        intentNechet.putExtra(LOC_MODEL_INTENT_FRAGMENT_NECHET, fragmentNechet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentNechet) }
    }

    private suspend fun mainUslNechet() = withContext(Dispatchers.Main){

        DialogManagerNechet.showDialogMainUslNechet(requireContext(),
            object : DialogManagerNechet.ListenerMainUslNechet{
                override fun onClickMainUslNechet(dialogUslNechet: String) {
                    if (dialogUslNechet == ""){
                        showToast("Вы не ввели значение")
                    }else{

                        mainUslNechet = dialogUslNechet.toFloat()

                        val fragmentNechet = FragmentModelUslNechet(
                            mainUslNechet
                        )
                        sendFragmentUslNechet(fragmentNechet)
                        showToast("Сохранено")
                    }
                }
            })
    }

    private fun sendFragmentUslNechet(fragmentNechet: FragmentModelUslNechet){
        val intentNechet = Intent(LOC_MODEL_INTENT_FRAGMENT_USL_NECHET)
        intentNechet.putExtra(LOC_MODEL_INTENT_FRAGMENT_USL_NECHET, fragmentNechet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentNechet) }
    }

    private fun centerLocationNechet(){
        binding.mapNechet.controller.animateTo(mLocOverlayNechet.myLocation)
        mLocOverlayNechet.enableFollowLocation()
    }

    @SuppressLint("DefaultLocale")
    private fun locationUpdateNechetFakt() = with(binding){
        modelNechet.locationUpdatesNechetFakt.observe(viewLifecycleOwner){
            val kmFaktNechet = "${String.format("%.3f", it.kmNechetFakt / 1000.0f)} км"
            val kmDistanceNechetFakt = "${it.kmDistanceNechetFakt} км ${it.pkDistanceNechetFakt} пк"
            val speedNechet = "${String.format("%.0f", 3.6f * it.speedNechet)} км/h"
            tvKmNechet.text = kmFaktNechet
            tvKmFaktNechet.text = kmDistanceNechetFakt

            val tvOgr15Nechet = it.tvOgrNechet15
            val tvOgr25Nechet = it.tvOgrNechet25
            val tvOgr40Nechet = it.tvOgrNechet40
            val tvOgr50Nechet = it.tvOgrNechet50
            val tvOgr55Nechet = it.tvOgrNechet55
            val tvOgr60Nechet = it.tvOgrNechet60
            val tvOgr65Nechet = it.tvOgrNechet65
            val tvOgr70Nechet = it.tvOgrNechet70
            val tvOgr75Nechet = it.tvOgrNechet75
            val tvKmPk15Nechet = it.tvKmPkNechet15
            val tvKmPk25Nechet = it.tvKmPkNechet25
            val tvKmPk40Nechet = it.tvKmPkNechet40
            val tvKmPk50Nechet = it.tvKmPkNechet50
            val tvKmPk55Nechet = it.tvKmPkNechet55
            val tvKmPk60Nechet = it.tvKmPkNechet60
            val tvKmPk65Nechet = it.tvKmPkNechet65
            val tvKmPk70Nechet = it.tvKmPkNechet70
            val tvKmPk75Nechet = it.tvKmPkNechet75
            tvSpeedNechet.text = speedNechet
            tvOgrNechet15.text = tvOgr15Nechet
            tvOgrNechet25.text = tvOgr25Nechet
            tvOgrNechet40.text = tvOgr40Nechet
            tvOgrNechet50.text = tvOgr50Nechet
            tvOgrNechet55.text = tvOgr55Nechet
            tvOgrNechet60.text = tvOgr60Nechet
            tvOgrNechet65.text = tvOgr65Nechet
            tvOgrNechet70.text = tvOgr70Nechet
            tvOgrNechet75.text = tvOgr75Nechet
            tvKmPkNechet15.text = tvKmPk15Nechet
            tvKmPkNechet25.text = tvKmPk25Nechet
            tvKmPkNechet40.text = tvKmPk40Nechet
            tvKmPkNechet50.text = tvKmPk50Nechet
            tvKmPkNechet55.text = tvKmPk55Nechet
            tvKmPkNechet60.text = tvKmPk60Nechet
            tvKmPkNechet65.text = tvKmPk65Nechet
            tvKmPkNechet70.text = tvKmPk70Nechet
            tvKmPkNechet75.text = tvKmPk75Nechet
            locationModelNechetFakt = it
        }
    }

    @SuppressLint("DefaultLocale")
    private fun locationUpdateNechet() = with(binding){
        modelNechet.locationUpdatesNechet.observe(viewLifecycleOwner){
            val distanceNechet = "${String.format("%.3f", it.distanceNechet / 1000.0f)} км"
            sbL = it.sbL
            val speedNechet = "${String.format("%.0f", 3.6f * it.speedNechet)} км/h"
            updatePolyline(it.geoPointsList)
            val kmDistanceNechet = "${it.kmDistanceNechet} км ${it.pkDistanceNechet} пк"
            tvDistanceNechet.text = distanceNechet
            tvSpeedNechet.text = speedNechet
            idKmNechet.text = kmDistanceNechet
            locationModelNechet = it

            if (binding.idKmNechet.text != "0 км 0 пк"){
                if (binding.saveMinusNechet50.isGone){
                    binding.saveMinusNechet50.isGone = binding.saveMinusNechet50.isVisible
                }
                if (binding.savePlusNechet50.isGone){
                    binding.savePlusNechet50.isGone = binding.savePlusNechet50.isVisible
                }
                if (binding.tvDistanceNechet.isGone){
                    binding.tvDistanceNechet.isGone = binding.tvDistanceNechet.isVisible
                }
                if (binding.tvSpeedNechet.isGone){
                    binding.tvSpeedNechet.isGone = binding.tvSpeedNechet.isVisible
                }
            } else {
                binding.tvOgrNechet15.text = ""
                binding.tvOgrNechet25.text = ""
                binding.tvOgrNechet40.text = ""
                binding.tvOgrNechet50.text = ""
                binding.tvOgrNechet55.text = ""
                binding.tvOgrNechet60.text = ""
                binding.tvOgrNechet65.text = ""
                binding.tvOgrNechet70.text = ""
                binding.tvOgrNechet75.text = ""

                binding.tvKmPkNechet15.text = ""
                binding.tvKmPkNechet25.text = ""
                binding.tvKmPkNechet40.text = ""
                binding.tvKmPkNechet50.text = ""
                binding.tvKmPkNechet55.text = ""
                binding.tvKmPkNechet60.text = ""
                binding.tvKmPkNechet65.text = ""
                binding.tvKmPkNechet70.text = ""
                binding.tvKmPkNechet75.text = ""
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//    }

//    override fun onPause() {
//        super.onPause()
//        modelNechet.locationUpdatesNechet.removeObservers(viewLifecycleOwner)
//    }

//    private suspend fun updateTimeNechet() = withContext(Dispatchers.Main){
//        modelNechet.timeDataNechet.observe(viewLifecycleOwner){
//            binding.tvTimeNechet.text = it
//        }
//    }

//    private suspend fun startTimerNechet() = withContext(Dispatchers.IO){
//        timerNechet?.cancel()
//        timerNechet = Timer()
//        startTimeNechet = LocationServiceNechet.startTimeNechet
//        timerNechet?.schedule(object : TimerTask(){
//            override fun run() {
//                activity?.runOnUiThread {
//                    modelNechet.timeDataNechet.value = getCurrentTimeNechet()
//                }
//            }
//
//        }, 1, 1)
//    }

//    private fun getCurrentTimeNechet(): String{
//        return "Время ${TimeUtils.getTime(System.currentTimeMillis() - startTimeNechet)}"
//    }

    private fun geoPointsToString(list: List<GeoPoint>): String {
        val sb = StringBuilder()
        val sbL = StringBuilder(sbL)
        Log.d("MyLog", "sbl: $sbL")

        var lastDistance = 0f
        list.forEachIndexed { index, item ->
            lastDistance = if (index == 0) {
                0f
            } else {
                val loc = Location("")
                val loc2 = Location("")
                loc.latitude = list[index - 1].latitude
                loc.longitude = list[index - 1].longitude

                loc2.latitude = item.latitude
                loc2.longitude = item.longitude
                lastDistance - loc.distanceTo(loc2)
            }
            sb.append("${item.latitude}, ${item.longitude}, $lastDistance /")

        }
        return sbL.toString()
    }

    private suspend fun startStopServiceNechet() = withContext(Dispatchers.Main){
        if (!isServiceRunningNechet){
            CoroutineScope(Dispatchers.IO).launch { startLocServiceNechet() }
            if (binding.mainDistanceNechet.isGone){
                binding.mainDistanceNechet.isGone = binding.mainDistanceNechet.isVisible
            }

            if (binding.saveMinusNechet50.isVisible){
                binding.saveMinusNechet50.isVisible = binding.saveMinusNechet50.isGone
            }
            if (binding.savePlusNechet50.isVisible){
                binding.savePlusNechet50.isVisible = binding.savePlusNechet50.isGone
            }
            if (binding.tvDistanceNechet.isVisible){
                binding.tvDistanceNechet.isVisible = binding.tvDistanceNechet.isGone
            }
            if (binding.tvSpeedNechet.isVisible){
                binding.tvSpeedNechet.isVisible = binding.tvSpeedNechet.isGone
            }

            if (binding.idKmNechet.isGone){
                binding.idKmNechet.text = "0 км 0 пк"
            }
            if (binding.idKmNechet.isGone){
                binding.idKmNechet.isGone = binding.idKmNechet.isVisible
            }
        } else {
            val track = getTrackItem()
            DialogManagerNechet.chowSaveDialog(requireContext(),
                track,
                object : DialogManagerNechet.ListenerTrackNechet{
                    override fun onClickTrackNechet() {
                        showToast("Track Saved!")
                        CoroutineScope(Dispatchers.IO).launch {
                            modelNechet.insertTrackNechet(track)
                        }
                    }

                })
            activity?.stopService(Intent(activity, LocationServiceNechet::class.java))
            binding.fStartStopNechet.setImageResource(R.drawable.is_play_nechet)
            if (binding.tvSpeedNechet.isVisible){
                binding.tvSpeedNechet.isVisible = binding.tvSpeedNechet.isGone
            }
            if (binding.idKmNechet.isVisible){
                binding.idKmNechet.isVisible = binding.idKmNechet.isGone
            }
            if (binding.tvDistanceNechet.isVisible){
                binding.tvDistanceNechet.isVisible = binding.tvDistanceNechet.isGone
            }
            if (binding.savePlusNechet50.isVisible){
                binding.savePlusNechet50.isVisible = binding.savePlusNechet50.isGone
            }
            if (binding.saveMinusNechet50.isVisible){
                binding.saveMinusNechet50.isVisible = binding.saveMinusNechet50.isGone
            }
            if (binding.mainDistanceNechet.isVisible){
                binding.mainDistanceNechet.isVisible = binding.mainDistanceNechet.isGone
            }

            binding.tvOgrNechet15.text = ""
            binding.tvOgrNechet25.text = ""
            binding.tvOgrNechet40.text = ""
            binding.tvOgrNechet50.text = ""
            binding.tvOgrNechet55.text = ""
            binding.tvOgrNechet60.text = ""
            binding.tvOgrNechet65.text = ""
            binding.tvOgrNechet70.text = ""
            binding.tvOgrNechet75.text = ""

            binding.tvKmPkNechet15.text = ""
            binding.tvKmPkNechet25.text = ""
            binding.tvKmPkNechet40.text = ""
            binding.tvKmPkNechet50.text = ""
            binding.tvKmPkNechet55.text = ""
            binding.tvKmPkNechet60.text = ""
            binding.tvKmPkNechet65.text = ""
            binding.tvKmPkNechet70.text = ""
            binding.tvKmPkNechet75.text = ""
//            timerNechet?.cancel()
        }
        isServiceRunningNechet = !isServiceRunningNechet
    }

    @SuppressLint("DefaultLocale")
    private fun getTrackItem(): TrackItemNechet {
        return  TrackItemNechet(
            null,
            "",
            String.format("%.3f", locationModelNechet?.distanceNechet?.div(1000.0f) ?: 0.0f),
            geoPointsToString((locationModelNechet?.geoPointsList ?: listOf()))

        )
    }

    private fun checkServiceStateNechet(){
        isServiceRunningNechet = LocationServiceNechet.isRunningNechet
        if (isServiceRunningNechet){
            binding.fStartStopNechet.setImageResource(R.drawable.is_stop_nechet)

            if (binding.mainDistanceNechet.isGone){
                binding.mainDistanceNechet.isGone = binding.mainDistanceNechet.isVisible
            }
//            CoroutineScope(Dispatchers.Main).launch { startTimerNechet() }
        } else {
            if (binding.saveMinusNechet50.isVisible){
                binding.saveMinusNechet50.isVisible = binding.saveMinusNechet50.isGone
            }
            if (binding.savePlusNechet50.isVisible){
                binding.savePlusNechet50.isVisible = binding.savePlusNechet50.isGone
            }
            if (binding.tvDistanceNechet.isVisible){
                binding.tvDistanceNechet.isVisible = binding.tvDistanceNechet.isGone
            }
            if (binding.tvSpeedNechet.isVisible){
                binding.tvSpeedNechet.isVisible = binding.tvSpeedNechet.isGone
            }

            if (binding.idKmNechet.isVisible){
                binding.idKmNechet.text = "0 км 0 пк"
            }
            if (binding.idKmNechet.isVisible){
                binding.idKmNechet.isVisible = binding.idKmNechet.isGone
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private suspend fun startLocServiceNechet() = withContext(Dispatchers.Main){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            activity?.stopService(Intent(activity, LocationServiceChet::class.java))
            activity?.startForegroundService(Intent(activity, LocationServiceNechet::class.java))
        }else{
            activity?.stopService(Intent(activity, LocationServiceChet::class.java))
            activity?.startService(Intent(activity, LocationServiceNechet::class.java))
        }
        binding.fStartStopNechet.setImageResource(R.drawable.is_stop_nechet)
//        LocationServiceNechet.startTimeNechet = System.currentTimeMillis()
//        CoroutineScope(Dispatchers.Main).launch { startTimerNechet() }
    }

    private suspend fun startStopServiceNechetFakt() = withContext(Dispatchers.Main) {
        if (!isServiceRunningNechetFakt && !LocationServiceNechetFakt.isRunningNechetFakt) {
            CoroutineScope(Dispatchers.IO).launch { startLocServiceNechetFakt() }

        } else {
            activity?.stopService(Intent(activity, LocationServiceNechetFakt::class.java))
            binding.fStartNechetFakt.setImageResource(R.drawable.is_start_nechet_fakt)
        }
        isServiceRunningNechetFakt = !isServiceRunningNechetFakt
    }

    private fun checkServiceStateNechetFakt(){
        isServiceRunningNechetFakt = LocationServiceNechetFakt.isRunningNechetFakt
        if (isServiceRunningNechetFakt){
            binding.fStartNechetFakt.setImageResource(R.drawable.is_stop_nechet_fakt)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private suspend fun startLocServiceNechetFakt() = withContext(Dispatchers.Main){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            activity?.stopService(Intent(activity, LocationServiceChetFakt::class.java))
            activity?.startForegroundService(Intent(activity, LocationServiceNechetFakt::class.java))
        }else{
            activity?.stopService(Intent(activity, LocationServiceChetFakt::class.java))
            activity?.startService(Intent(activity, LocationServiceNechetFakt::class.java))
        }
        binding.fStartNechetFakt.setImageResource(R.drawable.is_stop_nechet_fakt)
    }

    override fun onResume() {
        super.onResume()
        checkLocPermissionNechet()
        firstStart = true

        if (binding.saveMinusNechet50.isVisible){
            binding.saveMinusNechet50.isVisible = binding.saveMinusNechet50.isGone
        }
        if (binding.savePlusNechet50.isVisible){
            binding.savePlusNechet50.isVisible = binding.savePlusNechet50.isGone
        }
        if (binding.tvDistanceNechet.isVisible){
            binding.tvDistanceNechet.isVisible = binding.tvDistanceNechet.isGone
        }
        if (binding.tvSpeedNechet.isVisible){
            binding.tvSpeedNechet.isVisible = binding.tvSpeedNechet.isGone

            binding.tvOgrNechet15.text = ""
            binding.tvOgrNechet25.text = ""
            binding.tvOgrNechet40.text = ""
            binding.tvOgrNechet50.text = ""
            binding.tvOgrNechet55.text = ""
            binding.tvOgrNechet60.text = ""
            binding.tvOgrNechet65.text = ""
            binding.tvOgrNechet70.text = ""
            binding.tvOgrNechet75.text = ""

            binding.tvKmPkNechet15.text = ""
            binding.tvKmPkNechet25.text = ""
            binding.tvKmPkNechet40.text = ""
            binding.tvKmPkNechet50.text = ""
            binding.tvKmPkNechet55.text = ""
            binding.tvKmPkNechet60.text = ""
            binding.tvKmPkNechet65.text = ""
            binding.tvKmPkNechet70.text = ""
            binding.tvKmPkNechet75.text = ""
        }
    }

    private suspend fun settingsOsmNechet() = withContext(Dispatchers.Main){
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref_nechet", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOsmNechet() = with(binding){
        pl = Polyline()
        pl?.outlinePaint?.color = Color.BLUE
        mapNechet.controller.setZoom(20.0)
        val mLocProviderNechet = GpsMyLocationProvider(activity)
        mLocOverlayNechet = MyLocationNewOverlay(mLocProviderNechet, mapNechet)
        mLocOverlayNechet.enableMyLocation()
        mLocOverlayNechet.enableFollowLocation()
        mLocOverlayNechet.runOnFirstFix {
            mapNechet.overlays.clear()
            mapNechet.overlays.add(pl)
            mapNechet.overlays.add(mLocOverlayNechet)
        }
    }

    private fun registerPermissionsNechet(){
        pLauncherNechet = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){
            if(it[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                initOsmNechet()
                checkLocationEnabledNechet()
            } else {
                showToast("Вы не дали разрешения на использование местоположения! Nechet")
            }
        }
    }

    private fun checkLocPermissionNechet(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            checkPermissionAfter10Nechet()
        } else {
            checkPermissionBefore10Nechet()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionAfter10Nechet() {
        if (checkPermissionNechet(Manifest.permission.ACCESS_FINE_LOCATION)
            || checkPermissionNechet(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOsmNechet()
            checkLocationEnabledNechet()
        } else {
            pLauncherNechet.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun checkPermissionBefore10Nechet() {
        if (checkPermissionNechet(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOsmNechet()
            checkLocationEnabledNechet()
        } else {
            pLauncherNechet.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun checkLocationEnabledNechet(){
        val lManagerNechet = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabledNechet = lManagerNechet.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabledNechet){
            showToast("Геолокация не включена")
            DialogManagerNechet.showLocEnableDialogNechet(
                activity as AppCompatActivity,
                object : DialogManagerNechet.Listener{
                    override fun onClickNechet() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                }
            )
        } else {
            showToast("Геолокация включена")
        }
    }

    private val receiverNechet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationServiceNechet.LOC_MODEL_INTENT_NECHET){
                val locModelNechet = intent.getSerializableExtra(LocationServiceNechet.LOC_MODEL_INTENT_NECHET) as LocationModelNechet
                modelNechet.locationUpdatesNechet.value = locModelNechet
            }
        }
    }

    private fun registerLocReceiverNechet(){
        val locFilterNechet = IntentFilter(LocationServiceNechet.LOC_MODEL_INTENT_NECHET)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiverNechet, locFilterNechet)
    }

    private val receiverNechetFakt = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationServiceNechetFakt.LOC_MODEL_INTENT_NECHET_FAKT){
                val locModelNechetFakt = intent.getSerializableExtra(LocationServiceNechetFakt.LOC_MODEL_INTENT_NECHET_FAKT) as LocationModelNechetFakt
                modelNechet.locationUpdatesNechetFakt.value = locModelNechetFakt
            }
        }
    }

    private fun registerLocReceiverNechetFakt(){
        val locFilterNechetFakt = IntentFilter(LocationServiceNechetFakt.LOC_MODEL_INTENT_NECHET_FAKT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiverNechetFakt, locFilterNechetFakt)
    }

    private fun addPoint(list: List<GeoPoint>) {
        pl?.addPoint(list[list.size - 1])
    }

    private fun fillPolyline(list: List<GeoPoint>) {
        list.forEach {
            pl?.addPoint(it)
        }
    }

    private fun updatePolyline(list: List<GeoPoint>) {
        if (list.size > 1 && firstStart) {
            fillPolyline(list)
            firstStart = false
        } else {
            addPoint(list)
        }
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiverNechet)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiverNechetFakt)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragmentNechet()

        const val LOC_MODEL_INTENT_FRAGMENT_USL_NECHET = "fragment_intent_usl_nechet"
        const val LOC_MODEL_INTENT_FRAGMENT_NECHET = "fragment_intent_nechet"
        const val LOC_MODEL_INTENT_FRAGMENT_PLUS_NECHET = "fragment_intent_plus_nechet"
        const val LOC_MODEL_INTENT_FRAGMENT_MINUS_NECHET = "fragment_intent_minus_nechet"

    }
}