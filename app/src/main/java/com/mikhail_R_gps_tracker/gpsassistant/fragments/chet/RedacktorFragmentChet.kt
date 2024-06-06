package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikhail_R_gps_tracker.gpsassistant.databinding.RedacktorChetBinding

class RedacktorFragmentChet : Fragment() {
    private lateinit var binding:RedacktorChetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = RedacktorChetBinding.inflate(inflater, container, false)
        return binding.root
    }
}