package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikhail_R_gps_tracker.gpsassistant.databinding.PantographNechetBinding

class PantographFragmentNechet : Fragment() {
    private lateinit var binding: PantographNechetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = PantographNechetBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PantographFragmentNechet()

    }
}