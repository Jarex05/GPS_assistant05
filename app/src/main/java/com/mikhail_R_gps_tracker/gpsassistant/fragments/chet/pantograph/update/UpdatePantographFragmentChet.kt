package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.pantograph.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentUpdatePantographChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.MyDbManagerPantograph
import com.mikhail_R_gps_tracker.gpsassistant.utils.showToast

class UpdatePantographFragmentChet : Fragment() {
    private lateinit var binding: FragmentUpdatePantographChetBinding
    private val args by navArgs<UpdatePantographFragmentChetArgs>()
    private lateinit var myDbManagerPantograph: MyDbManagerPantograph

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdatePantographChetBinding.inflate(inflater, container, false)

        binding.edUpdateStartPantographChet.setText(args.itemPantographChet.startChet.toString())
        binding.edUpdatePkStartPantographChet.setText(args.itemPantographChet.picketStartChet.toString())

        binding.fbUpdateCancelPantographChet.setOnClickListener {
            findNavController().navigate(R.id.action_updatePantographFragmentChet_to_listPantographFragmentChet)
        }

        binding.fbUpdateSavePantographChet.setOnClickListener {
            updateChet()
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

    private fun updateChet() {
        val startKm = (binding.edUpdateStartPantographChet.text.ifEmpty{ 0 }.toString()).toInt()
        val startPk = (binding.edUpdatePkStartPantographChet.text.ifEmpty{ 0 }.toString()).toInt()

        if (startKm != 0 && startPk != 0){
            if (startKm.toString() != "" && startPk.toString() != "" && startPk.toString() == "1" || startPk.toString() == "2" || startPk.toString() == "3" || startPk.toString() == "4" || startPk.toString() == "5" || startPk.toString() == "6" || startPk.toString() == "7" || startPk.toString() == "8" || startPk.toString() == "9" || startPk.toString() == "10"){
                myDbManagerPantograph.updateDbDataPantographChet(startKm, startPk, args.itemPantographChet.idChet)
                findNavController().navigate(R.id.action_updatePantographFragmentChet_to_listPantographFragmentChet)
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
        fun newInstance() = UpdatePantographFragmentChet()
    }
}