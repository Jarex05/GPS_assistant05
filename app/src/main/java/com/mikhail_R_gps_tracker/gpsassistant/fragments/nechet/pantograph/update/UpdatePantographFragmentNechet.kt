package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.pantograph.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentUpdatePantographNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.MyDbManagerPantograph
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast

class UpdatePantographFragmentNechet : Fragment() {
    private lateinit var binding: FragmentUpdatePantographNechetBinding
    private val args by navArgs<UpdatePantographFragmentNechetArgs>()
    private lateinit var myDbManagerPantograph: MyDbManagerPantograph

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdatePantographNechetBinding.inflate(inflater, container, false)

        binding.edUpdateStartPantographNechet.setText(args.itemPantographNechet.startNechet.toString())
        binding.edUpdatePkStartPantographNechet.setText(args.itemPantographNechet.picketStartNechet.toString())

        binding.fbUpdateCancelPantographNechet.setOnClickListener {
            findNavController().navigate(R.id.action_updatePantographFragmentNechet_to_listPantographFragmentNechet)
        }

        binding.fbUpdateSavePantographNechet.setOnClickListener {
            updateNechet()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(v: View) {
        myDbManagerPantograph = MyDbManagerPantograph(v.context)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerPantograph.openDb()
    }

    private fun updateNechet() {
        val startKm = (binding.edUpdateStartPantographNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.edUpdatePkStartPantographNechet.text.ifEmpty{ 0 }.toString()).toInt()

        if (startKm != 0 && startPk != 0){
            if (startKm.toString() != "" && startPk.toString() != "" && startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                myDbManagerPantograph.updateDbDataPantographNechet(startKm, startPk, args.itemPantographNechet.idNechet)
                findNavController().navigate(R.id.action_updatePantographFragmentNechet_to_listPantographFragmentNechet)
            }
            else{
                showToast("Поле 'Пикет' должно содержать число не менее '1' и не более '10'")
            }
        } else {
            showToast("Вы не ввели значения для сохранения!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerPantograph.closeDb()
    }

    companion object {
        @JvmStatic
        fun newInstance() = UpdatePantographFragmentNechet()
    }
}