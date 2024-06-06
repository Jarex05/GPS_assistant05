package com.mikhail_R_gps_tracker.gpsassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ActivityNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.BrakeFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.LimitationsFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.MainFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.PantographFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.RedacktorFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.TracksFragmentNechet
import com.mikhail_R_gps_tracker.gpsassistant.utils.openFragmentNechet
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.common.AdRequest

class ActivityNechet : AppCompatActivity() {
    private lateinit var binding: ActivityNechetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNechetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Реклама Yandex
        binding.bannerNechet.setAdUnitId("demo-banner-yandex")
        binding.bannerNechet.setAdSize(BannerAdSize.stickySize(this, 300))
        val adRequest = AdRequest.Builder().build()
        binding.bannerNechet.loadAd(adRequest)

        onBottomNavClicksNechet()
        openFragmentNechet(MainFragmentNechet.newInstance())
    }

    private fun onBottomNavClicksNechet() {
        binding.bNavNechet.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.id_home_nechet -> openFragmentNechet(MainFragmentNechet.newInstance())
                R.id.id_limitations_nechet -> openFragmentNechet(LimitationsFragmentNechet())
                R.id.id_pantograph_nechet -> openFragmentNechet(PantographFragmentNechet.newInstance())
                R.id.id_brake_nechet -> openFragmentNechet(BrakeFragmentNechet.newInstance())
                R.id.id_tracks_nechet -> openFragmentNechet(TracksFragmentNechet.newInstance())
//                R.id.id_redacktor_nechet -> openFragmentNechet(RedacktorFragmentNechet())
            }
            true
        }
    }
}