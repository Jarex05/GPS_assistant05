package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikhail_R_gps_tracker.gpsassistant.databinding.LimitationsNechetBinding

class LimitationsFragmentNechet : Fragment() {
    private lateinit var binding: LimitationsNechetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LimitationsNechetBinding.inflate(inflater, container, false)
        return binding.root
    }
}