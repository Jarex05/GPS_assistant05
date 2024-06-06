package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.brake.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentAddBrakeNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast

class AddBrakeFragmentNechet : Fragment() {
    private lateinit var binding: FragmentAddBrakeNechetBinding
    private lateinit var myDbManagerBrake: MyDbManagerBrake

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddBrakeNechetBinding.inflate(inflater, container, false)

        binding.fbSaveBrakeNechet.setOnClickListener {
            onClickSave()
        }

        binding.fbCancelBrakeNechet.setOnClickListener {
            findNavController().navigate(R.id.action_addBrakeFragmentNechet_to_listBrakeFragmentNechet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerBrake.openDb()
    }

    private fun init(v: View) {
        myDbManagerBrake = MyDbManagerBrake(v.context)
    }

    private fun onClickSave() {
        val startKm = (binding.edStartBrakeNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.edPkStartBrakeNechet.text.ifEmpty{ 0 }.toString()).toInt()

        if (startKm != 0 && startPk != 0){
            if (startKm.toString() != "" && startPk.toString() != ""){
                if (startKm.toString() != "" && startPk.toString() != "" && startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                    myDbManagerBrake.insertToDbBrakeNechet(startKm, startPk)
                    findNavController().navigate(R.id.action_addBrakeFragmentNechet_to_listBrakeFragmentNechet)
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
        fun newInstance() = AddBrakeFragmentNechet()
    }
}