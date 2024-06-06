package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.redacktor.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentUpdateRedacktorChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast

class UpdateRedacktorFragmentChet : Fragment() {
    private lateinit var binding: FragmentUpdateRedacktorChetBinding
    private val args by navArgs<UpdateRedacktorFragmentChetArgs>()
    private lateinit var myDbManagerRedacktor: MyDbManagerRedacktor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdateRedacktorChetBinding.inflate(inflater, container, false)

        binding.updateStartRedacktorChet.setText(args.itemRedacktorChet.startChet.toString())
        binding.updatePkStartRedacktorChet.setText(args.itemRedacktorChet.picketStartChet.toString())
        binding.updateMinusRedacktorChet.setText(args.itemRedacktorChet.minusChet)
        binding.updatePlusRedacktorChet.setText(args.itemRedacktorChet.plusChet)

        binding.fbUpdateCancelRedacktorChet.setOnClickListener {
            findNavController().navigate(R.id.action_updateRedacktorFragmentChet_to_listRedacktorFragmentChet)
        }

        binding.fbUpdateSaveRedacktorChet.setOnClickListener {
            updateChet()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(v: View) {
        myDbManagerRedacktor = MyDbManagerRedacktor(v.context)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerRedacktor.openDb()
    }

    private fun updateChet() {
        val startKm = (binding.updateStartRedacktorChet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.updatePkStartRedacktorChet.text.ifEmpty{ 0 }.toString()).toInt()
        val minus = binding.updateMinusRedacktorChet.text
        val plus = binding.updatePlusRedacktorChet.text

        if (startKm != 0 && startPk != 0){
            if (startKm.toString() != "" && startPk.toString() != ""){
                if (minus.toString() == "" && plus.toString() != ""){
                    if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                        myDbManagerRedacktor.updateDbDataRedacktorChet(startKm, startPk, minus.toString(), plus.toString(), args.itemRedacktorChet.idChet)
                        findNavController().navigate(R.id.action_updateRedacktorFragmentChet_to_listRedacktorFragmentChet)
                        showToast("Сохранено!")
                    }
                    else{
                        showToast("Поле 'Пикет' должно содержать число не менее '1' и не более '10'")
                    }
                }
                if (minus.toString() != "" && plus.toString() == ""){
                    if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                        myDbManagerRedacktor.updateDbDataRedacktorChet(startKm, startPk, minus.toString(), plus.toString(), args.itemRedacktorChet.idChet)
                        findNavController().navigate(R.id.action_updateRedacktorFragmentChet_to_listRedacktorFragmentChet)
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
        fun newInstance() = UpdateRedacktorFragmentChet()
    }
}