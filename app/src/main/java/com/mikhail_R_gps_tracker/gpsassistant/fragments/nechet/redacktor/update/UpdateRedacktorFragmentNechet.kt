package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.redacktor.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentUpdateRedacktorNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast

class UpdateRedacktorFragmentNechet : Fragment() {
    private lateinit var binding: FragmentUpdateRedacktorNechetBinding
    private val args by navArgs<UpdateRedacktorFragmentNechetArgs>()
    private lateinit var myDbManagerRedacktor: MyDbManagerRedacktor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdateRedacktorNechetBinding.inflate(inflater, container, false)

        binding.updateStartRedacktorNechet.setText(args.itemRedacktorNechet.startNechet.toString())
        binding.updatePkStartRedacktorNechet.setText(args.itemRedacktorNechet.picketStartNechet.toString())
        binding.updateMinusRedacktorNechet.setText(args.itemRedacktorNechet.minusNechet)
        binding.updatePlusRedacktorNechet.setText(args.itemRedacktorNechet.plusNechet)

        binding.fbUpdateCancelRedacktorNechet.setOnClickListener {
            findNavController().navigate(R.id.action_updateRedacktorFragmentNechet_to_listRedacktorFragmentNechet)
        }

        binding.fbUpdateSaveRedacktorNechet.setOnClickListener {
            updateNechet()
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

    private fun updateNechet() {
        val startKm = (binding.updateStartRedacktorNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.updatePkStartRedacktorNechet.text.ifEmpty{ 0 }.toString()).toInt()
        val minus = binding.updateMinusRedacktorNechet.text
        val plus = binding.updatePlusRedacktorNechet.text

        if (startKm != 0 && startPk != 0){
            if (startKm.toString() != "" && startPk.toString() != ""){
                if (minus.toString() == "" && plus.toString() != ""){
                    if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                        myDbManagerRedacktor.updateDbDataRedacktorNechet(startKm, startPk, minus.toString(), plus.toString(), args.itemRedacktorNechet.idNechet)
                        findNavController().navigate(R.id.action_updateRedacktorFragmentNechet_to_listRedacktorFragmentNechet)
                        showToast("Сохранено!")
                    }
                    else{
                        showToast("Поле 'Пикет' должно содержать число не менее '1' и не более '10'")
                    }
                }
                if (minus.toString() != "" && plus.toString() == ""){
                    if (startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                        myDbManagerRedacktor.updateDbDataRedacktorNechet(startKm, startPk, minus.toString(), plus.toString(), args.itemRedacktorNechet.idNechet)
                        findNavController().navigate(R.id.action_updateRedacktorFragmentNechet_to_listRedacktorFragmentNechet)
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
        fun newInstance() = UpdateRedacktorFragmentNechet()
    }
}