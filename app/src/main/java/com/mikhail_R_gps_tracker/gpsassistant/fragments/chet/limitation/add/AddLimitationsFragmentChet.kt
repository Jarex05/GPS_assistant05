package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.limitation.add

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentAddLimitationsChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddLimitationsFragmentChet : Fragment() {
    private lateinit var binding: FragmentAddLimitationsChetBinding
    private lateinit var myDbManagerLimitations: MyDbManagerLimitations

    private var titleStartChet: Int = 0
    private var piketStartChet: Int = 0
    private var titleFinishChet: Int = 0
    private var piketFinishChet: Int = 0
    private var speedChet: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddLimitationsChetBinding.inflate(inflater, container, false)

        binding.fbSaveLimitationsChet.setOnClickListener {
            onClickSave()
        }

        binding.fbCancelLimitationsChet.setOnClickListener {
            findNavController().navigate(R.id.action_addLimitationsFragmentChet_to_listLimitationsFragmentChet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerLimitations.openDb()
    }

    private fun init(v: View) {
        myDbManagerLimitations = MyDbManagerLimitations(v.context)
    }

    private fun onClickSave() {
        val startKm = (binding.edStartLimitationsChet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.edPkStartLimitationsChet.text.ifEmpty{ 0 }.toString()).toInt()
        val finishKm = (binding.edFinishLimitationsChet.text.ifEmpty{ 0 }.toString()).toInt()
        val finishPk = (binding.edPkFinishLimitationsChet.text.ifEmpty{ 0 }.toString()).toInt()
        val speed = (binding.edSpeedLimitationsChet.text.ifEmpty{ 0 }.toString()).toInt()

        if (startKm != 0 && startPk != 0 && finishKm != 0 && finishPk != 0 && speed != 0){
            if (startKm.toString() != "" && startPk.toString() != "" && finishKm.toString() != "" && finishPk.toString() != "" && speed.toString() != ""){
                if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                    if (finishPk.toString() == "1" || finishPk.toString() == "2" || finishPk.toString() == "3" || finishPk.toString() == "4" || finishPk.toString() == "5" || finishPk.toString() == "6" || finishPk.toString() == "7" || finishPk.toString() == "8" || finishPk.toString() == "9" || finishPk.toString() == "10"){
                        if (startKm < finishKm){
                            myDbManagerLimitations.insertToDbLimitationsChet(startKm, startPk, finishKm, finishPk, speed)
                            findNavController().navigate(R.id.action_addLimitationsFragmentChet_to_listLimitationsFragmentChet)
                            showToast("Сохранено!")
                        }
                        if (startKm == finishKm){
                            if (startPk < finishPk){
                                myDbManagerLimitations.insertToDbLimitationsChet(startKm, startPk, finishKm, finishPk, speed)
                                findNavController().navigate(R.id.action_addLimitationsFragmentChet_to_listLimitationsFragmentChet)
                                showToast("Сохранено!")
                            }
                            if (startPk == finishPk){
                                myDbManagerLimitations.insertToDbLimitationsChet(startKm, startPk, finishKm, finishPk, speed)
                                findNavController().navigate(R.id.action_addLimitationsFragmentChet_to_listLimitationsFragmentChet)
                                showToast("Сохранено!")
                            }
                            if (startPk > finishPk){
                                showToast("Некорректные данные!")
                            }
                        }
                        if (startKm > finishKm){
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
        fun newInstance() = AddLimitationsFragmentChet()
    }
}