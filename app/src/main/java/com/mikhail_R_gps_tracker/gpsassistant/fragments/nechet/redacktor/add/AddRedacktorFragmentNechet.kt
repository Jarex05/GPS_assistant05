package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.redacktor.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentAddRedacktorNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast
import kotlin.math.min

class AddRedacktorFragmentNechet : Fragment() {
    private lateinit var binding: FragmentAddRedacktorNechetBinding
    private lateinit var myDbManagerRedacktor: MyDbManagerRedacktor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddRedacktorNechetBinding.inflate(inflater, container, false)

        binding.fbSaveRedacktorNechet.setOnClickListener {
            onClickSave()
        }

        binding.fbCancelRedacktorNechet.setOnClickListener {
            findNavController().navigate(R.id.action_addRedacktorFragmentNechet_to_listRedacktorFragmentNechet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerRedacktor.openDb()
    }

    private fun init(v: View) {
        myDbManagerRedacktor = MyDbManagerRedacktor(v.context)
    }

    private fun onClickSave() {
        val startKm = (binding.edStartRedacktorNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.edPkStartRedacktorNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val minus = binding.edMinusRedacktorNechet.text
        val plus = binding.edPlusRedacktorNechet.text

        if (startKm != 0 && startPk != 0){
            if (startKm.toString() != "" && startPk.toString() != ""){
                if (minus.toString() == "" && plus.toString() != ""){
                    if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                        myDbManagerRedacktor.insertToDbRedacktorNechet(startKm, startPk, minus.toString(), plus.toString())
                        findNavController().navigate(R.id.action_addRedacktorFragmentNechet_to_listRedacktorFragmentNechet)
                        showToast("Сохранено!")
                    }
                    else{
                        showToast("Поле 'Пикет' должно содержать число не менее '1' и не более '10'")
                    }
                }
                if (minus.toString() != "" && plus.toString() == ""){
                    if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9"|| startPk.toString() == "10"){
                        myDbManagerRedacktor.insertToDbRedacktorNechet(startKm, startPk, minus.toString(), plus.toString())
                        findNavController().navigate(R.id.action_addRedacktorFragmentNechet_to_listRedacktorFragmentNechet)
                        showToast("Сохранено!")
                    }
                    else{
                        showToast("Поле 'Пикет' должно содержать число не менее '1' и не более '10'")
                    }
                }
                if (minus.toString() != "" && plus.toString() != ""){
                    showToast("Одно из полей 'МИНУС' или 'ПЛЮС' должно быть пустым!")
                } else {
                    showToast("Заполните поле 'ПЛЮС' или 'МИНУС' значением!")
                }
            }
        } else {
            showToast("Вы не ввели значения для сохранения!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerRedacktor.closeDb()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddRedacktorFragmentNechet()
    }
}