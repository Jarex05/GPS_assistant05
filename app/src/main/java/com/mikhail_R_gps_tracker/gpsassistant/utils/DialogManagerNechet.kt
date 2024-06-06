package com.mikhail_R_gps_tracker.gpsassistant.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.MainDistanceNechetDialogBinding
import com.mikhail_R_gps_tracker.gpsassistant.databinding.MainUslNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.databinding.SaveDialogBinding
import com.mikhail_R_gps_tracker.gpsassistant.databinding.SaveDialogNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItem
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItemNechet

object DialogManagerNechet {

    fun chowSaveDialog(context: Context, item: TrackItemNechet?, listenerTrackNechet: ListenerTrackNechet){
        val builder = AlertDialog.Builder(context)
        val binding = SaveDialogNechetBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialogSave = builder.create()
        binding.apply {

            val distance = "${item?.distanceNechet} км"
            tvDistanceNechet.text = distance

            bSaveNechet.setOnClickListener {
                val dialogTitleTrackNechet = binding.idTrackTitleNechet.text.toString()
                if (dialogTitleTrackNechet == ""){
                    Toast.makeText(context, "Введите название маршрута!", Toast.LENGTH_LONG).show()
                } else {
                    item?.titleNechet = dialogTitleTrackNechet
                    listenerTrackNechet.onClickTrackNechet()
                    dialogSave.dismiss()
                }
            }
            bCancelNechet.setOnClickListener {
                dialogSave.dismiss()
            }
            dialogSave.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogSave.show()
        }
    }

    fun showLocEnableDialogNechet(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location_disabled_nechet)
        dialog.setMessage(context.getString(R.string.location_dialog_message_nechet))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да"){
                _, _ -> listener.onClickNechet()
        }

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет"){
                _, _ -> dialog.dismiss()
        }
        dialog.show()
    }

    fun showDialogMainDistanceNechet(context: Context, listenerMainDistanceNechet: ListenerMainDistanceNechet){
        val builderNechet = AlertDialog.Builder(context)
        val binding = MainDistanceNechetDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builderNechet.setView(binding.root)
        val dialogNechet = builderNechet.create()
        binding.apply {
            dialogMainDistanceSaveNechet.setOnClickListener {
                val dialogDistanceNechet = binding.dialogMainDistanceNechet.text.toString()
                dialogMainDistanceNechet.setText(dialogDistanceNechet)

                listenerMainDistanceNechet.onClickMainDistanceNechet(dialogDistanceNechet)
                dialogNechet.dismiss()
            }
            dialogMainDistanceCancelNechet.setOnClickListener {
                dialogNechet.dismiss()
            }
        }
        dialogNechet.show()
    }

    fun showDialogMainUslNechet(context: Context, listenerMainUslNechet: ListenerMainUslNechet){
        val builderNechet = AlertDialog.Builder(context)
        val binding = MainUslNechetBinding.inflate(LayoutInflater.from(context), null, false)
        builderNechet.setView(binding.root)
        val dialogNechet = builderNechet.create()
        binding.apply {
            MainDialogUslSaveNechet.setOnClickListener {
                val dialogUslNechet = binding.mainDialogUslNechet.text.toString()
                mainDialogUslNechet.setText(dialogUslNechet)

                listenerMainUslNechet.onClickMainUslNechet(dialogUslNechet)
                dialogNechet.dismiss()
            }
            MainDialogUslCancelNechet.setOnClickListener {
                dialogNechet.dismiss()
            }
        }
        dialogNechet.show()
    }

    interface ListenerTrackNechet{
        fun onClickTrackNechet()
    }

    interface Listener{
        fun onClickNechet()
    }

    interface ListenerMainDistanceNechet{
        fun onClickMainDistanceNechet(dialogDistanceNechet: String)
    }

    interface ListenerMainUslNechet{
        fun onClickMainUslNechet(dialogUslNechet: String)
    }
}