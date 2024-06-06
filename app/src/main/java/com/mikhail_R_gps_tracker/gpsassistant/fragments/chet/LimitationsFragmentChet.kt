package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mikhail_R_gps_tracker.gpsassistant.databinding.LimitationsChetBinding

class LimitationsFragmentChet : Fragment() {
    private lateinit var binding: LimitationsChetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LimitationsChetBinding.inflate(inflater, container, false)
        return binding.root
    }
}