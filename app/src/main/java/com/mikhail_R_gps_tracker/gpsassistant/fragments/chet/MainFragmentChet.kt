package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet

//import com.yandex.mobile.ads.banner.BannerAdEventListener
//import com.yandex.mobile.ads.banner.BannerAdSize
//import com.yandex.mobile.ads.common.AdRequest
//import com.yandex.mobile.ads.common.AdRequestError
//import com.yandex.mobile.ads.common.AdSize
//import com.yandex.mobile.ads.common.ImpressionData
//import com.yandex.mobile.ads.common.MobileAds
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
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mikhail_R_gps_tracker.gpsassistant.MainApp
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentMainChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItem
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentMinusChet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentModelChet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentModelUslChet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentPlusChet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelChet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationModelChetFakt
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceChet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceChetFakt
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceNechet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceNechetFakt
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewChet
import com.mikhail_R_gps_tracker.gpsassistant.utils.DialogManagerChet
import com.mikhail_R_gps_tracker.gpsassistant.utils.checkPermissionChet
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.Delay
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Suppress("DEPRECATION", "UNREACHABLE_CODE")
class MainFragmentChet : Fragment() {
    private var pl: Polyline? = null
    private var sbL = StringBuilder("")
    private lateinit var tvDistancePrimerChet: TextView
    private lateinit var tvDistanceChet: TextView
    private var mainDistanceChet = 0.0f
    private var mainUslChet = 0.0f
    private var savePlus50 = 0.0f
    private var saveMinus50 = 0.0f

    private var locationModelChet: LocationModelChet? = null
    private var locationModelChetFakt: LocationModelChetFakt? = null
    private var isServiceRunningChet = false
    private var isServiceRunningChetFakt = false
    private var firstStart = true
//    private var timerChet: Timer? = null
//    private var startTimeChet = 0L
    private lateinit var mLocOverlayChet: MyLocationNewOverlay
    private lateinit var pLauncherChet: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMainChetBinding
    private val modelChet: ModelViewChet by activityViewModels{
        ModelViewChet.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        CoroutineScope(Dispatchers.IO).launch { settingsOsmChet() }
        binding = FragmentMainChetBinding.inflate(inflater, container, false)
        return binding.root

        tvDistancePrimerChet = tvDistancePrimerChet
        tvDistanceChet = tvDistanceChet
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPermissionsChet()
        setOnClicksChet()
        checkServiceStateChet()
        checkServiceStateChetFakt()
//        CoroutineScope(Dispatchers.IO).launch { updateTimeChet() }
        registerLocReceiverChet()
        registerLocReceiverChetFakt()
        locationUpdateChetFakt()
        locationUpdateChet()
    }

    private fun setOnClicksChet() = with(binding){
        val listenerChet = onClicksChet()
        fStartStopChet.setOnClickListener(listenerChet)
        fStartChetFakt.setOnClickListener(listenerChet)
        fCenterChet.setOnClickListener(listenerChet)
        mainDistanceChet.setOnClickListener(listenerChet)
        mainUslChet.setOnClickListener(listenerChet)
        savePlusChet50.setOnClickListener(listenerChet)
        saveMinusChet50.setOnClickListener(listenerChet)
    }

    private fun onClicksChet(): OnClickListener {
        return OnClickListener {
            when(it.id){
                R.id.fStartStopChet -> CoroutineScope(Dispatchers.IO).launch { startStopServiceChet() }
                R.id.fStartChetFakt -> CoroutineScope(Dispatchers.IO).launch { startStopServiceChetFakt() }
                R.id.fCenterChet -> centerLocationChet()
                R.id.mainDistanceChet -> CoroutineScope(Dispatchers.IO).launch { mainDistanceChet() }
                R.id.mainUslChet -> CoroutineScope(Dispatchers.IO).launch { mainUslChet() }
                R.id.saveMinusChet50 -> CoroutineScope(Dispatchers.IO).launch { saveMinus() }
                R.id.savePlusChet50 -> CoroutineScope(Dispatchers.IO).launch { savePlus() }
            }
        }
    }

    private suspend fun saveMinus() = withContext(Dispatchers.Main) {
        saveMinus50 = 20.0f
        val fragmentMinusChet = FragmentMinusChet(
            saveMinus50
        )
        sendFragmentMinusChet(fragmentMinusChet)
    }

    private fun sendFragmentMinusChet(fragmentMinusChet: FragmentMinusChet){
        val intentChet = Intent(LOC_MODEL_INTENT_FRAGMENT_MINUS_CHET)
        intentChet.putExtra(LOC_MODEL_INTENT_FRAGMENT_MINUS_CHET, fragmentMinusChet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentChet) }
    }

    private suspend fun savePlus() = withContext(Dispatchers.Main) {
        savePlus50 = 20.0f
        val fragmentPlusChet = FragmentPlusChet(
            savePlus50
        )
        sendFragmentPlusChet(fragmentPlusChet)
    }

    private fun sendFragmentPlusChet(fragmentPlusChet: FragmentPlusChet){
        val intentChet = Intent(LOC_MODEL_INTENT_FRAGMENT_PLUS_CHET)
        intentChet.putExtra(LOC_MODEL_INTENT_FRAGMENT_PLUS_CHET, fragmentPlusChet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentChet) }
    }

    private suspend fun mainDistanceChet() = withContext(Dispatchers.Main){

        DialogManagerChet.showDialogMainDistanceChet(requireContext(),
            object : DialogManagerChet.ListenerMainDistanceChet{
                override fun onClickMainDistanceChet(dialogDistanceChet: String) {
                    if (dialogDistanceChet == ""){
                        showToast("Вы не ввели значение")
                    }else{

                        mainDistanceChet = dialogDistanceChet.toFloat()

                        if (binding.saveMinusChet50.isGone){
                            binding.saveMinusChet50.isGone = binding.saveMinusChet50.isVisible
                        }
                        if (binding.savePlusChet50.isGone){
                            binding.savePlusChet50.isGone = binding.savePlusChet50.isVisible
                        }
                        if (binding.tvDistanceChet.isGone){
                            binding.tvDistanceChet.isGone = binding.tvDistanceChet.isVisible
                        }
                        if (binding.tvSpeedChet.isGone){
                            binding.tvSpeedChet.isGone = binding.tvSpeedChet.isVisible
                        }

                        val fragmentChet = FragmentModelChet(
                            mainDistanceChet,
                        )
                        sendFragmentChet(fragmentChet)
                        showToast("Сохранено")
                    }
                }
            })
    }

    private fun sendFragmentChet(fragmentChet: FragmentModelChet){
        val intentChet = Intent(LOC_MODEL_INTENT_FRAGMENT_CHET)
        intentChet.putExtra(LOC_MODEL_INTENT_FRAGMENT_CHET, fragmentChet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentChet) }
    }

    private suspend fun mainUslChet() = withContext(Dispatchers.Main){

        DialogManagerChet.showDialogMainUslChet(requireContext(),
            object : DialogManagerChet.ListenerMainUslChet{
                override fun onClickMainUslChet(dialogUslChet: String) {
                    if (dialogUslChet == ""){
                        showToast("Вы не ввели значение")
                    }else{
                        mainUslChet = dialogUslChet.toFloat()

                        val fragmentChet = FragmentModelUslChet(
                            mainUslChet
                        )
                        sendFragmentUslChet(fragmentChet)
                        showToast("Сохранено")
                    }
                }
            })
    }

    private fun sendFragmentUslChet(fragmentChet: FragmentModelUslChet){
        val intentChet = Intent(LOC_MODEL_INTENT_FRAGMENT_USL_CHET)
        intentChet.putExtra(LOC_MODEL_INTENT_FRAGMENT_USL_CHET, fragmentChet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentChet) }
    }

    private fun centerLocationChet(){
        binding.mapChet.controller.animateTo(mLocOverlayChet.myLocation)
        mLocOverlayChet.enableFollowLocation()
    }

    @SuppressLint("DefaultLocale")
    private fun locationUpdateChetFakt() = with(binding){
        modelChet.locationUpdatesChetFakt.observe(viewLifecycleOwner){
            val kmFakt = "${String.format("%.3f", it.kmChetFakt / 1000.0f)} км"
            val kmDistanceChetFakt = "${it.kmDistanceChetFakt} км ${it.pkDistanceChetFakt} пк"
            val speedChet = "${String.format("%.0f", 3.6f * it.speedChet)} км/h"
            tvKmChet.text = kmFakt
            tvKmFakt.text = kmDistanceChetFakt

            val tvOgr15Chet = it.tvOgrChet15
            val tvOgr25Chet = it.tvOgrChet25
            val tvOgr40Chet = it.tvOgrChet40
            val tvOgr50Chet = it.tvOgrChet50
            val tvOgr55Chet = it.tvOgrChet55
            val tvOgr60Chet = it.tvOgrChet60
            val tvOgr65Chet = it.tvOgrChet65
            val tvOgr70Chet = it.tvOgrChet70
            val tvOgr75Chet = it.tvOgrChet75
            val tvKmPk15Chet = it.tvKmPkChet15
            val tvKmPk25Chet = it.tvKmPkChet25
            val tvKmPk40Chet = it.tvKmPkChet40
            val tvKmPk50Chet = it.tvKmPkChet50
            val tvKmPk55Chet = it.tvKmPkChet55
            val tvKmPk60Chet = it.tvKmPkChet60
            val tvKmPk65Chet = it.tvKmPkChet65
            val tvKmPk70Chet = it.tvKmPkChet70
            val tvKmPk75Chet = it.tvKmPkChet75
            tvSpeedChet.text = speedChet
            tvOgrChet15.text = tvOgr15Chet
            tvOgrChet25.text = tvOgr25Chet
            tvOgrChet40.text = tvOgr40Chet
            tvOgrChet50.text = tvOgr50Chet
            tvOgrChet55.text = tvOgr55Chet
            tvOgrChet60.text = tvOgr60Chet
            tvOgrChet65.text = tvOgr65Chet
            tvOgrChet70.text = tvOgr70Chet
            tvOgrChet75.text = tvOgr75Chet
            tvKmPkChet15.text = tvKmPk15Chet
            tvKmPkChet25.text = tvKmPk25Chet
            tvKmPkChet40.text = tvKmPk40Chet
            tvKmPkChet50.text = tvKmPk50Chet
            tvKmPkChet55.text = tvKmPk55Chet
            tvKmPkChet60.text = tvKmPk60Chet
            tvKmPkChet65.text = tvKmPk65Chet
            tvKmPkChet70.text = tvKmPk70Chet
            tvKmPkChet75.text = tvKmPk75Chet
            locationModelChetFakt = it
        }
    }


    @SuppressLint("DefaultLocale")
    private fun locationUpdateChet() = with(binding){
        modelChet.locationUpdatesChet.observe(viewLifecycleOwner){
            val distanceChet = "${String.format("%.3f", it.distanceChet / 1000.0f)} км"
            sbL = it.sbL
            val speedChet = "${String.format("%.0f", 3.6f * it.speedChet)} км/h"
            updatePolyline(it.geoPointsList)
            val kmDistanceChet = "${it.kmDistanceChet} км ${it.pkDistanceChet} пк"
            tvDistanceChet.text = distanceChet
            tvSpeedChet.text = speedChet
            idKmChet.text = kmDistanceChet
            locationModelChet = it

            if (binding.idKmChet.text != "0 км 0 пк"){
                if (binding.saveMinusChet50.isGone){
                    binding.saveMinusChet50.isGone = binding.saveMinusChet50.isVisible
                }
                if (binding.savePlusChet50.isGone){
                    binding.savePlusChet50.isGone = binding.savePlusChet50.isVisible
                }
                if (binding.tvDistanceChet.isGone){
                    binding.tvDistanceChet.isGone = binding.tvDistanceChet.isVisible
                }
                if (binding.tvSpeedChet.isGone){
                    binding.tvSpeedChet.isGone = binding.tvSpeedChet.isVisible
                }
            } else {
                binding.tvOgrChet15.text = ""
                binding.tvOgrChet25.text = ""
                binding.tvOgrChet40.text = ""
                binding.tvOgrChet50.text = ""
                binding.tvOgrChet55.text = ""
                binding.tvOgrChet60.text = ""
                binding.tvOgrChet65.text = ""
                binding.tvOgrChet70.text = ""
                binding.tvOgrChet75.text = ""

                binding.tvKmPkChet15.text = ""
                binding.tvKmPkChet25.text = ""
                binding.tvKmPkChet40.text = ""
                binding.tvKmPkChet50.text = ""
                binding.tvKmPkChet55.text = ""
                binding.tvKmPkChet60.text = ""
                binding.tvKmPkChet65.text = ""
                binding.tvKmPkChet70.text = ""
                binding.tvKmPkChet75.text = ""
            }
        }
    }

//    private suspend fun updateTimeChet() = withContext(Dispatchers.Main){
//        modelChet.timeDataChet.observe(viewLifecycleOwner){
//            binding.tvTimeChet.text = it
//        }
//    }

//    private suspend fun startTimerChet() = withContext(Dispatchers.IO){
//        timerChet?.cancel()
//        timerChet = Timer()
//        startTimeChet = LocationServiceChet.startTimeChet
//        timerChet?.schedule(object : TimerTask(){
//            override fun run() {
//                activity?.runOnUiThread {
//                    modelChet.timeDataChet.value = getCurrentTimeChet()
//                }
//            }
//
//        }, 1, 1)
//    }

//    private fun getCurrentTimeChet(): String{
//        return "Время ${TimeUtils.getTime(System.currentTimeMillis() - startTimeChet)}"
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
                lastDistance + loc.distanceTo(loc2)
            }
            sb.append("${item.latitude}, ${item.longitude}, $lastDistance /")

        }
        return sbL.toString()
    }

    private suspend fun startStopServiceChet() = withContext(Dispatchers.Main){
        if (!isServiceRunningChet && !LocationServiceChet.isRunningChet){
            CoroutineScope(Dispatchers.IO).launch { startLocServiceChet() }

            if (binding.mainDistanceChet.isGone){
                binding.mainDistanceChet.isGone = binding.mainDistanceChet.isVisible
            }

            if (binding.saveMinusChet50.isVisible){
                binding.saveMinusChet50.isVisible = binding.saveMinusChet50.isGone
            }
            if (binding.savePlusChet50.isVisible){
                binding.savePlusChet50.isVisible = binding.savePlusChet50.isGone
            }
            if (binding.tvDistanceChet.isVisible){
                binding.tvDistanceChet.isVisible = binding.tvDistanceChet.isGone
            }
            if (binding.tvSpeedChet.isVisible){
                binding.tvSpeedChet.isVisible = binding.tvSpeedChet.isGone
            }

            if (binding.idKmChet.isGone){
                binding.idKmChet.text = "0 км 0 пк"
            }
            if (binding.idKmChet.isGone){
                binding.idKmChet.isGone = binding.idKmChet.isVisible
            }

        } else {
            val track = getTrackItem()
            DialogManagerChet.chowSaveDialog(requireContext(),
                track,
                object : DialogManagerChet.ListenerTrack{
                    override fun onClickTrackChet() {
                        showToast("Track Saved!")
                        CoroutineScope(Dispatchers.IO).launch {
                            modelChet.insertTrack(track)
                        }

                        activity?.stopService(Intent(activity, LocationServiceChet::class.java))
                        binding.fStartStopChet.setImageResource(R.drawable.is_play_chet)

                        binding.tvDistanceChet.hide()
                        binding.saveMinusChet50.hide()
                        binding.savePlusChet50.hide()
                        binding.mainDistanceChet.hide()
                        binding.idKmChet.hide()
                        binding.tvSpeedChet.hide()

//                        binding.tvDistanceChet.isVisible = false
//                        binding.saveMinusChet50.isVisible = false
//                        binding.savePlusChet50.isVisible = false
//                        binding.mainDistanceChet.isVisible = false
//                        binding.idKmChet.isVisible = false
//                        binding.tvSpeedChet.isVisible = false

//                        if (binding.tvSpeedChet.isVisible){
//                            binding.tvSpeedChet.isVisible = binding.tvSpeedChet.isGone
//                        }
//                        if (binding.idKmChet.isVisible){
//                            binding.idKmChet.isVisible = binding.idKmChet.isGone
//                        }
//                        if (binding.tvDistanceChet.isVisible){
//                            binding.tvDistanceChet.isVisible = binding.tvDistanceChet.isGone
//                        }
//                        if (binding.savePlusChet50.isVisible){
//                            binding.savePlusChet50.isVisible = binding.savePlusChet50.isGone
//                        }
//                        if (binding.saveMinusChet50.isVisible){
//                            binding.saveMinusChet50.isVisible = binding.saveMinusChet50.isGone
//                        }
//                        if (binding.mainDistanceChet.isVisible){
//                            binding.mainDistanceChet.isVisible = binding.mainDistanceChet.isGone
//                        }

                        binding.tvOgrChet15.text = ""
                        binding.tvOgrChet25.text = ""
                        binding.tvOgrChet40.text = ""
                        binding.tvOgrChet50.text = ""
                        binding.tvOgrChet55.text = ""
                        binding.tvOgrChet60.text = ""
                        binding.tvOgrChet65.text = ""
                        binding.tvOgrChet70.text = ""
                        binding.tvOgrChet75.text = ""

                        binding.tvKmPkChet15.text = ""
                        binding.tvKmPkChet25.text = ""
                        binding.tvKmPkChet40.text = ""
                        binding.tvKmPkChet50.text = ""
                        binding.tvKmPkChet55.text = ""
                        binding.tvKmPkChet60.text = ""
                        binding.tvKmPkChet65.text = ""
                        binding.tvKmPkChet70.text = ""
                        binding.tvKmPkChet75.text = ""
                    }

                })

//            timerChet?.cancel()
        }
        isServiceRunningChet = !isServiceRunningChet
    }

    fun View.hide() {
        visibility = GONE
    }

    @SuppressLint("DefaultLocale")
    private fun getTrackItem(): TrackItem{
        return  TrackItem(
            null,
            "",
            String.format("%.3f", locationModelChet?.distanceChet?.div(1000.0f) ?: 0.0f),
            geoPointsToString((locationModelChet?.geoPointsList ?: listOf()))

        )
    }

    private fun checkServiceStateChet(){
        isServiceRunningChet = LocationServiceChet.isRunningChet
        if (isServiceRunningChet){
            binding.fStartStopChet.setImageResource(R.drawable.is_stop_chet)

            if (binding.mainDistanceChet.isGone){
                binding.mainDistanceChet.isGone = binding.mainDistanceChet.isVisible
            }
//            CoroutineScope(Dispatchers.Main).launch { startTimerChet() }
        } else {
            if (binding.saveMinusChet50.isVisible){
                binding.saveMinusChet50.isVisible = binding.saveMinusChet50.isGone
            }
            if (binding.savePlusChet50.isVisible){
                binding.savePlusChet50.isVisible = binding.savePlusChet50.isGone
            }
            if (binding.tvDistanceChet.isVisible){
                binding.tvDistanceChet.isVisible = binding.tvDistanceChet.isGone
            }
            if (binding.tvSpeedChet.isVisible){
                binding.tvSpeedChet.isVisible = binding.tvSpeedChet.isGone
            }

            if (binding.idKmChet.isVisible){
                binding.idKmChet.text = "0 км 0 пк"
            }
            if (binding.idKmChet.isVisible){
                binding.idKmChet.isVisible = binding.idKmChet.isGone
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private suspend fun startLocServiceChet() = withContext(Dispatchers.Main){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            activity?.stopService(Intent(activity, LocationServiceNechet::class.java))
            activity?.startForegroundService(Intent(activity, LocationServiceChet::class.java))
        }else{
            activity?.stopService(Intent(activity, LocationServiceNechet::class.java))
            activity?.startService(Intent(activity, LocationServiceChet::class.java))
        }
        binding.fStartStopChet.setImageResource(R.drawable.is_stop_chet)
//        LocationServiceChet.startTimeChet = System.currentTimeMillis()
//        CoroutineScope(Dispatchers.Main).launch { startTimerChet() }
    }

    private suspend fun startStopServiceChetFakt() = withContext(Dispatchers.Main) {
        if (!isServiceRunningChetFakt && !LocationServiceChetFakt.isRunningChetFakt) {
            CoroutineScope(Dispatchers.IO).launch { startLocServiceChetFakt() }

        } else {
            activity?.stopService(Intent(activity, LocationServiceChetFakt::class.java))
            binding.fStartChetFakt.setImageResource(R.drawable.is_start_chet_fakt)
        }
        isServiceRunningChetFakt = !isServiceRunningChetFakt
    }

    private fun checkServiceStateChetFakt(){
        isServiceRunningChetFakt = LocationServiceChetFakt.isRunningChetFakt
        if (isServiceRunningChetFakt){
            binding.fStartChetFakt.setImageResource(R.drawable.is_stop_chet_fakt)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private suspend fun startLocServiceChetFakt() = withContext(Dispatchers.Main){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            activity?.stopService(Intent(activity, LocationServiceNechetFakt::class.java))
            activity?.startForegroundService(Intent(activity, LocationServiceChetFakt::class.java))
        }else{
            activity?.stopService(Intent(activity, LocationServiceNechetFakt::class.java))
            activity?.startService(Intent(activity, LocationServiceChetFakt::class.java))
        }
        binding.fStartChetFakt.setImageResource(R.drawable.is_stop_chet_fakt)
    }

    override fun onResume() {
        super.onResume()
        checkLocPermissionChet()
        firstStart = true

        if (binding.saveMinusChet50.isVisible){
            binding.saveMinusChet50.isVisible = binding.saveMinusChet50.isGone
        }
        if (binding.savePlusChet50.isVisible){
            binding.savePlusChet50.isVisible = binding.savePlusChet50.isGone
        }
        if (binding.tvDistanceChet.isVisible){
            binding.tvDistanceChet.isVisible = binding.tvDistanceChet.isGone
        }
        if (binding.tvSpeedChet.isVisible){
            binding.tvSpeedChet.isVisible = binding.tvSpeedChet.isGone

            binding.tvOgrChet15.text = ""
            binding.tvOgrChet25.text = ""
            binding.tvOgrChet40.text = ""
            binding.tvOgrChet50.text = ""
            binding.tvOgrChet55.text = ""
            binding.tvOgrChet60.text = ""
            binding.tvOgrChet65.text = ""
            binding.tvOgrChet70.text = ""
            binding.tvOgrChet75.text = ""

            binding.tvKmPkChet15.text = ""
            binding.tvKmPkChet25.text = ""
            binding.tvKmPkChet40.text = ""
            binding.tvKmPkChet50.text = ""
            binding.tvKmPkChet55.text = ""
            binding.tvKmPkChet60.text = ""
            binding.tvKmPkChet65.text = ""
            binding.tvKmPkChet70.text = ""
            binding.tvKmPkChet75.text = ""
        }

    }

    private suspend fun settingsOsmChet() = withContext(Dispatchers.Main){
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref_chet", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    private fun initOsmChet() = with(binding){
        pl = Polyline()
        pl?.outlinePaint?.color = Color.BLUE
        mapChet.controller.setZoom(19.0)
        val mLocProviderChet = GpsMyLocationProvider(activity)
        mLocOverlayChet = MyLocationNewOverlay(mLocProviderChet, mapChet)
        mLocOverlayChet.enableMyLocation()
        mLocOverlayChet.enableFollowLocation()
        mLocOverlayChet.runOnFirstFix {
            mapChet.overlays.clear()
            mapChet.overlays.add(pl)
            mapChet.overlays.add(mLocOverlayChet)
        }
    }

    private fun registerPermissionsChet(){
        pLauncherChet = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){
            if(it[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                initOsmChet()
                checkLocationEnabledChet()
            } else {
                showToast("Вы не дали разрешения на использование местоположения! Chet")
            }
        }
    }

    private fun checkLocPermissionChet(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            checkPermissionAfter10Chet()
        } else {
            checkPermissionBefore10Chet()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissionAfter10Chet() {
        if (checkPermissionChet(Manifest.permission.ACCESS_FINE_LOCATION)
            || checkPermissionChet(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOsmChet()
            checkLocationEnabledChet()
        } else {
            pLauncherChet.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }

    private fun checkPermissionBefore10Chet() {
        if (checkPermissionChet(Manifest.permission.ACCESS_FINE_LOCATION)) {
            initOsmChet()
            checkLocationEnabledChet()
        } else {
            pLauncherChet.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun checkLocationEnabledChet(){
        val lManagerChet = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabledChet = lManagerChet.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isEnabledChet){
            showToast("Геолокация не включена")
            DialogManagerChet.showLocEnableDialogChet(
                activity as AppCompatActivity,
                object : DialogManagerChet.Listener{
                    override fun onClickChet() {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                }
            )
        } else {
            showToast("Геолокация включена")
        }
    }

    private val receiverChet = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationServiceChet.LOC_MODEL_INTENT_CHET){
                val locModelChet = intent.getSerializableExtra(LocationServiceChet.LOC_MODEL_INTENT_CHET) as LocationModelChet
                modelChet.locationUpdatesChet.value = locModelChet
            }
        }
    }



    private fun registerLocReceiverChet(){
        val locFilterChet = IntentFilter(LocationServiceChet.LOC_MODEL_INTENT_CHET)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiverChet, locFilterChet)
    }


    private val receiverChetFakt = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == LocationServiceChetFakt.LOC_MODEL_INTENT_CHET_FAKT){
                val locModelChetFakt = intent.getSerializableExtra(LocationServiceChetFakt.LOC_MODEL_INTENT_CHET_FAKT) as LocationModelChetFakt
                modelChet.locationUpdatesChetFakt.value = locModelChetFakt
            }
        }
    }

    private fun registerLocReceiverChetFakt(){
        val locFilterChetFakt = IntentFilter(LocationServiceChetFakt.LOC_MODEL_INTENT_CHET_FAKT)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .registerReceiver(receiverChetFakt, locFilterChetFakt)
    }

    private fun addPoint(list: List<GeoPoint>){
        pl?.addPoint(list[list.size - 1])
    }

    private fun fillPolyline(list: List<GeoPoint>){
        list.forEach {
            pl?.addPoint(it)
        }
    }

    private fun updatePolyline(list: List<GeoPoint>){
        if (list.size > 1 && firstStart){
            fillPolyline(list)
            firstStart = false
        } else {
            addPoint(list)
        }
    }

    override fun onDetach() {
        super.onDetach()
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiverChet)
        LocalBroadcastManager.getInstance(activity as AppCompatActivity)
            .unregisterReceiver(receiverChetFakt)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragmentChet()

        const val LOC_MODEL_INTENT_FRAGMENT_USL_CHET = "fragment_intent_usl_chet"
        const val LOC_MODEL_INTENT_FRAGMENT_CHET = "fragment_intent_chet"
        const val LOC_MODEL_INTENT_FRAGMENT_PLUS_CHET = "fragment_intent_plus_chet"
        const val LOC_MODEL_INTENT_FRAGMENT_MINUS_CHET = "fragment_intent_minus_chet"

    }
}
