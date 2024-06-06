package com.mikhail_R_gps_tracker.gpsassistant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ActivityMainBinding
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdSize

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.chet.alpha=0f
        binding.nechet.alpha=0f

        binding.chet.animate().alpha(1f).translationXBy((-50).toFloat()).setStartDelay(300).duration=1500
        binding.nechet.animate().alpha(1f).translationXBy((50).toFloat()).setStartDelay(300).duration=1500

        // Реклама Yandex

        binding.banner.setAdUnitId("demo-banner-yandex")
        binding.banner.setAdSize(BannerAdSize.stickySize(this, 300))
        val adRequest = AdRequest.Builder().build()
        binding.banner.loadAd(adRequest)
    }

    fun onClickChet(view: View) {
        val intentChet = Intent(this, ActivityChet::class.java)
        startActivity(intentChet)
    }

    fun onClickNechet(view: View) {
        val intentNechet = Intent(this, ActivityNechet::class.java)
        startActivity(intentNechet)
    }
}