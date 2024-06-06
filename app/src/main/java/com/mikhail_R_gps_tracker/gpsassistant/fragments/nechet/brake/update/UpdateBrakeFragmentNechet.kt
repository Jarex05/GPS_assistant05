package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.brake.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentUpdateBrakeNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast

class UpdateBrakeFragmentNechet : Fragment() {
    private lateinit var binding: FragmentUpdateBrakeNechetBinding
    private val args by navArgs<UpdateBrakeFragmentNechetArgs>()
    private lateinit var myDbManagerBrake: MyDbManagerBrake

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdateBrakeNechetBinding.inflate(inflater, container, false)

        binding.edUpdateStartBrakeNechet.setText(args.itemBrakeNechet.startNechet.toString())
        binding.edUpdatePkStartBrakeNechet.setText(args.itemBrakeNechet.picketStartNechet.toString())

        binding.fbUpdateCancelBrakeNechet.setOnClickListener {
            findNavController().navigate(R.id.action_updateBrakeFragmentNechet_to_listBrakeFragmentNechet)
        }

        binding.fbUpdateSaveBrakeNechet.setOnClickListener {
            updateNechet()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(v: View) {
        myDbManagerBrake = MyDbManagerBrake(v.context)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerBrake.openDb()
    }

    private fun updateNechet() {
        val startKm = (binding.edUpdateStartBrakeNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.edUpdatePkStartBrakeNechet.text.ifEmpty{ 0 }.toString()).toInt()

        if (startKm != 0 && startPk != 0){
            if (startKm.toString() != "" && startPk.toString() != ""){
                if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                    myDbManagerBrake.updateDbDataBrakeNechet(startKm, startPk, args.itemBrakeNechet.idNechet)
                    findNavController().navigate(R.id.action_updateBrakeFragmentNechet_to_listBrakeFragmentNechet)
                }
                else{
                    showToast("Поле 'Пикет' должно содержать число не менее '1' и не более '10'")
                }
            }
        } else {
            showToast("Вы не ввели значения для сохранения!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerBrake.closeDb()
    }

    companion object {
        @JvmStatic
        fun newInstance() = UpdateBrakeFragmentNechet()
    }
}