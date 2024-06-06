package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.limitation.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentUpdateLimitationsNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast

class UpdateLimitationsFragmentNechet : Fragment() {
    private lateinit var binding: FragmentUpdateLimitationsNechetBinding
    private val args by navArgs<UpdateLimitationsFragmentNechetArgs>()
    private lateinit var myDbManagerLimitations: MyDbManagerLimitations

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdateLimitationsNechetBinding.inflate(inflater, container, false)

        binding.edUpdateStartLimitationsNechet.setText(args.itemLimitationsNechet.startNechet.toString())
        binding.edUpdatePkStartLimitationsNechet.setText(args.itemLimitationsNechet.picketStartNechet.toString())
        binding.edUpdateFinishLimitationsNechet.setText(args.itemLimitationsNechet.finishNechet.toString())
        binding.edUpdatePkFinishLimitationsNechet.setText(args.itemLimitationsNechet.picketFinishNechet.toString())
        binding.edUpdateSpeedLimitationsNechet.setText(args.itemLimitationsNechet.speedNechet.toString())

        binding.fbUpdateCancelLimitationsNechet.setOnClickListener {
            findNavController().navigate(R.id.action_updateLimitationsFragmentNechet_to_listLimitationsFragmentNechet)
        }

        binding.fbUpdateSaveLimitationsNechet.setOnClickListener {
            updateNechet()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(v: View) {
        myDbManagerLimitations = MyDbManagerLimitations(v.context)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerLimitations.openDb()
    }

    private fun updateNechet() {
        val startKm = (binding.edUpdateStartLimitationsNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.edUpdatePkStartLimitationsNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val finishKm = (binding.edUpdateFinishLimitationsNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val finishPk = (binding.edUpdatePkFinishLimitationsNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val speed = (binding.edUpdateSpeedLimitationsNechet.text.ifEmpty{ 0 }.toString()).toInt()

        if (startKm != 0 && startPk != 0 && finishKm != 0 && finishPk != 0 && speed != 0){
            if (startKm.toString() != "" && startPk.toString() != "" && finishKm.toString() != "" && finishPk.toString() != "" && speed.toString() != ""){
                if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                    if (finishPk.toString() == "1" || finishPk.toString() == "2" || finishPk.toString() == "3" || finishPk.toString() == "4" || finishPk.toString() == "5" || finishPk.toString() == "6" || finishPk.toString() == "7" || finishPk.toString() == "8" || finishPk.toString() == "9" || finishPk.toString() == "10"){
                        if (startKm > finishKm){
                            myDbManagerLimitations.updateDbDataLimitationsNechet(startKm, startPk, finishKm, finishPk, speed, args.itemLimitationsNechet.idNechet)
                            findNavController().navigate(R.id.action_updateLimitationsFragmentNechet_to_listLimitationsFragmentNechet)
                            showToast("Сохранено!")
                        }
                        if (startKm == finishKm){
                            if (startPk > finishPk){
                                myDbManagerLimitations.updateDbDataLimitationsNechet(startKm, startPk, finishKm, finishPk, speed, args.itemLimitationsNechet.idNechet)
                                findNavController().navigate(R.id.action_updateLimitationsFragmentNechet_to_listLimitationsFragmentNechet)
                                showToast("Сохранено!")
                            }
                            if (startPk == finishPk){
                                myDbManagerLimitations.updateDbDataLimitationsNechet(startKm, startPk, finishKm, finishPk, speed, args.itemLimitationsNechet.idNechet)
                                findNavController().navigate(R.id.action_updateLimitationsFragmentNechet_to_listLimitationsFragmentNechet)
                                showToast("Сохранено!")
                            }
                            if (startPk < finishPk){
                                showToast("Некорректные данные!")
                            }
                        }
                        if (startKm < finishKm){
                            showToast("Некорректные данные!")
                        }
                    }
                    else{
                        showToast("Поле 'Пикет' должно содержать число не менее '1' и не более '10'")
                    }
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
        myDbManagerLimitations.closeDb()
    }

    companion object {
        @JvmStatic
        fun newInstance() = UpdateLimitationsFragmentNechet()
    }
}