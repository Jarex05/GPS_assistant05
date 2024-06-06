package com.mikhail_R_gps_tracker.gpsassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ActivityChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.BrakeFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.LimitationsFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.MainFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.PantographFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.RedacktorFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.TracksFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.utils.openFragmentChet
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.common.AdRequest

class ActivityChet : AppCompatActivity() {
    private lateinit var binding: ActivityChetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Реклама Yandex
        binding.bannerChet.setAdUnitId("demo-banner-yandex")
        binding.bannerChet.setAdSize(BannerAdSize.stickySize(this, 300))
        val adRequest = AdRequest.Builder().build()
        binding.bannerChet.loadAd(adRequest)

        onBottomNavClicksChet()
        openFragmentChet(MainFragmentChet.newInstance())
    }

    private fun onBottomNavClicksChet() {
        binding.bNavChet.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.id_home_chet -> openFragmentChet(MainFragmentChet.newInstance())
                R.id.id_limitations_chet -> openFragmentChet(LimitationsFragmentChet())
                R.id.id_pantograph_chet -> openFragmentChet(PantographFragmentChet.newInstance())
                R.id.id_brake_chet -> openFragmentChet(BrakeFragmentChet.newInstance())
                R.id.id_tracks_chet -> openFragmentChet(TracksFragmentChet.newInstance())
//                R.id.id_redacktor_chet -> openFragmentChet(RedacktorFragmentChet())
            }
            true
        }
    }
}