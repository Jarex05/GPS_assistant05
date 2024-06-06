package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mikhail_R_gps_tracker.gpsassistant.MainApp
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentViewTrackNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.ViewTrackFragment
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentLatLongKmToServiceChet
import com.mikhail_R_gps_tracker.gpsassistant.location.FragmentLatLongKmToServiceNechet
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewChet
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewNechet
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

class ViewTrackFragmentNechet : Fragment() {
    private val polylineNechet = Polyline()
    private val distanceArrayNechet = ArrayList<Float>()

    private lateinit var binding: FragmentViewTrackNechetBinding
    private val modelNechet: ModelViewNechet by activityViewModels{
        ModelViewNechet.ViewModelFactory((requireContext().applicationContext as MainApp).databaseNechet)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        CoroutineScope(Dispatchers.IO).launch { settingsOsmNechet() }
        binding = FragmentViewTrackNechetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTrackNechet()
    }

    private fun getTrackNechet() = with(binding){
        modelNechet.currentTrackNechet.observe(viewLifecycleOwner){
            val distance = "${it.distanceNechet} км"
            tvDistanceSaveNechet.text = distance
            val polyline = getPolylineNechet(it.geoPointsNechet)
            mapNechet.overlays.add(polyline)
            goToStartPositionNechet(polyline.actualPoints[0])
            initMyLocNechet()
        }
    }

    private fun goToStartPositionNechet(startPositionNechet: GeoPoint){
        binding.mapNechet.controller.zoomTo(16.0)
        binding.mapNechet.controller.animateTo(startPositionNechet)
    }

    private fun initMyLocNechet(){
        val mLocProviderNechet = GpsMyLocationProvider(activity)
        val mLocOverlayNechet = MyLocationNewOverlay(mLocProviderNechet, binding.mapNechet)
        mLocOverlayNechet.enableMyLocation()
        mLocOverlayNechet.enableFollowLocation()
        binding.mapNechet.overlays.add(mLocOverlayNechet)
//        updateLocation(mLocOverlayChet)
    }

    private fun getPolylineNechet(geoPointsNechet: String): Polyline {

        val list = geoPointsNechet.split("/")
        distanceArrayNechet.clear()
        list.forEach {
            if (it.isEmpty()) return@forEach
            val points = it.split(",")
            polylineNechet.addPoint(GeoPoint(points[0].toDouble(), points[1].toDouble()))
            distanceArrayNechet.add(points[2].toFloat())
//            Log.d("MyLog", "latitude: ${points[0]}")
//            Log.d("MyLog", "longitude: ${points[1]}")
//            Log.d("MyLog", "Киллометр: ${points[2]}")

//            activity?.startService(Intent(activity, LocationServiceChet::class.java).apply {
//                putExtra("latLongKm", geoPoints)
//            })



            val fragmentLatLongKmToServiceNechet = FragmentLatLongKmToServiceNechet(
                geoPointsNechet,

                )
            sendFragmentLatLongKmToServiceNechet(fragmentLatLongKmToServiceNechet)
        }
        return polylineNechet
    }

    private fun sendFragmentLatLongKmToServiceNechet(fragmentLatLongKmToServiceNechet: FragmentLatLongKmToServiceNechet){
        val intentLatLongKmToServiceNechet = Intent(FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET_FAKT)
        intentLatLongKmToServiceNechet.putExtra(FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET_FAKT, fragmentLatLongKmToServiceNechet)
        context?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intentLatLongKmToServiceNechet) }
    }

    private suspend fun settingsOsmNechet() = withContext(Dispatchers.Main){
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            activity?.getSharedPreferences("osm_pref_nechet", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    companion object {
        @JvmStatic
        fun newInstance() = ViewTrackFragmentNechet()

        const val FRAGMENT_LAT_LONG_KM_TO_SERVICE_NECHET_FAKT = "fragment_lat_long_km_to_service_nechet_fakt"
    }
}