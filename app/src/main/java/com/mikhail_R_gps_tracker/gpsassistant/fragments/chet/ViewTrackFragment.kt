package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mikhail_R_gps_tracker.gpsassistant.MainApp
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentViewTrackBinding
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentLatLongKmToServiceChet
import com.mikhail_R_gps_tracker.gpsassistant.location.LocationServiceChet
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewChet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.StringBuilder
import java.util.Timer
import java.util.TimerTask

class ViewTrackFragment : Fragment() {
    private val polyline = Polyline()
//    private var locationTimer: Timer? = null
    private val distanceArray = ArrayList<Float>()

    private lateinit var binding: FragmentViewTrackBinding
    private val modelChet: ModelViewChet by activityViewModels{
        ModelViewChet.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        CoroutineScope(Dispatchers.IO).launch { settingsOsmChet() }
        binding = FragmentViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTrack()
//        activity?.startService(Intent(activity, LocationServiceChet::class.java))
    }

    private fun getTrack() = with(binding){
        modelChet.currentTrack.observe(viewLifecycleOwner){
            val distance = "${it.distance} км"
            tvDistanceSave.text = distance
            val polyline = getPolyline(it.geoPoints)
            map.overlays.add(polyline)
            goToStartPosition(polyline.actualPoints[0])
            initMyLoc()
        }
    }

    private fun goToStartPosition(startPosition: GeoPoint){
        binding.map.controller.zoomTo(16.0)
        binding.map.controller.animateTo(startPosition)
    }

    private fun initMyLoc(){
        val mLocProviderChet = GpsMyLocationProvider(activity)
        val mLocOverlayChet = MyLocationNewOverlay(mLocProviderChet, binding.map)
        mLocOverlayChet.enableMyLocation()
        mLocOverlayChet.enableFollowLocation()
        binding.map.overlays.add(mLocOverlayChet)
//        updateLocation(mLocOverlayChet)
    }

    private fun getPolyline(geoPoints: String): Polyline {

        val list = geoPoints.split("/")
        distanceArray.clear()
        list.forEach {
            if (it.isEmpty()) return@forEach
            val points = it.split(",")
            polyline.addPoint(GeoPoint(points[0].toDouble(), points[1].toDouble()))
            distanceArray.add(points[2].toFloat())
//            Log.d("MyLog", "latitude: ${points[0]}")
//            Log.d("MyLog", "longitude: ${points[1]}")
//            Log.d("MyLog", "Киллометр: ${points[2]}")

//            activity?.startService(Intent(activity, LocationServiceChet::class.java).apply {
//                putExtra("latLongKm", geoPoints)
//            })



            val fragmentLatLongKmToService = FragmentLatLongKmToServiceChet(
                geoPoints,

                )
            sendFragmentLatLongKmToService(fragmentLatLongKmToService)
        }
        return polyline
    }

    private fun sendFragmentLatLongKmToService(fragmentLatLongKmToService: FragmentLatLongKmToServiceChet){
        val intentLatLongKmToService = Intent(FRAGMENT_LAT_LONG_KM_TO_SERVICE_FAKT)
        intentLatLongKmToService.putExtra(FRAGMENT_LAT_LONG_KM_TO_SERVICE_FAKT, fragmentLatLongKmToService)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentLatLongKmToService) }
    }

//    private fun updateLocation(myLocOverlay: MyLocationNewOverlay){
//        locationTimer?.cancel()
//        locationTimer = Timer()
//        locationTimer?.schedule(object : TimerTask(){
//            override fun run() {
//                val myCurrentLoc = myLocOverlay.myLocation
//                val mLoc = Location("").apply {
//                    latitude = myCurrentLoc.latitude
//                    longitude = myCurrentLoc.longitude
//                }
//                var minDistance = 100f
//                var distanceIndex = 0
//
//                for (i in polyline.actualPoints.indices){
//                    val pointLoc = Location("").apply {
//                        latitude = polyline.actualPoints[i].latitude
//                        longitude = polyline.actualPoints[i].longitude
//                    }
//                    val dist = mLoc.distanceTo(pointLoc)
//                    if (dist < 10){
//                        if (minDistance > dist){
//                            minDistance = dist
//                            distanceIndex = i
//                        }
//                    }
//                }
//
//                if (minDistance < 10){
//                    requireActivity().runOnUiThread{
//                        Toast.makeText(requireContext(), "Distance ${distanceArray[distanceIndex]}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//        }, 1000, 1000)
//    }

    private suspend fun settingsOsmChet() = withContext(Dispatchers.Main){
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref_chet", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTrackFragment()

        const val FRAGMENT_LAT_LONG_KM_TO_SERVICE_FAKT = "fragment_lat_long_km_to_service_fakt"
    }

    override fun onPause() {
        super.onPause()
//        locationTimer?.cancel()
    }
}