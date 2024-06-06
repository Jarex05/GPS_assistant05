package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mikhail_R_gps_tracker.gpsassistant.databinding.PantographChetBinding

class PantographFragmentChet : Fragment() {
    private lateinit var binding: PantographChetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = PantographChetBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PantographFragmentChet()

    }
}