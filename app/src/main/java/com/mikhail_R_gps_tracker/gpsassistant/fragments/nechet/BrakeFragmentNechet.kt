package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikhail_R_gps_tracker.gpsassistant.databinding.BrakeNechetBinding

class BrakeFragmentNechet : Fragment() {
    private lateinit var binding: BrakeNechetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BrakeNechetBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrakeFragmentNechet()

    }
}