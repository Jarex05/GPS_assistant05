package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mikhail_R_gps_tracker.gpsassistant.databinding.BrakeChetBinding

class BrakeFragmentChet : Fragment() {
    private lateinit var binding: BrakeChetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BrakeChetBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrakeFragmentChet()

    }
}