package com.mikhail_R_gps_tracker.gpsassistant.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.MainDistanceChetDialogBinding
import com.mikhail_R_gps_tracker.gpsassistant.databinding.MainUslChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.databinding.SaveDialogBinding
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItem

object DialogManagerChet {

    fun chowSaveDialog(context: Context, item: TrackItem?, listenerTrack: ListenerTrack){
        val builder = AlertDialog.Builder(context)
        val binding = SaveDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialogSave = builder.create()
        binding.apply {

            val distance = "${item?.distance} км"
            tvDistance.text = distance

            bSave.setOnClickListener {
                val dialogTitleTrackChet = binding.idTrackTitle.text.toString()
                if (dialogTitleTrackChet == ""){
                    Toast.makeText(context, "Введите название маршрута!", Toast.LENGTH_LONG).show()
                } else {
                    item?.title = dialogTitleTrackChet
                    listenerTrack.onClickTrackChet()
                    dialogSave.dismiss()
                }
            }
            bCancel.setOnClickListener {
                dialogSave.dismiss()
            }
            dialogSave.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialogSave.show()
        }
    }

    fun showLocEnableDialogChet(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(R.string.location_disabled_chet)
        dialog.setMessage(context.getString(R.string.location_dialog_message_chet))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да"){
                _, _ -> listener.onClickChet()
        }

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет"){
                _, _ -> dialog.dismiss()
        }
        dialog.show()
    }

    fun showDialogMainDistanceChet(context: Context, listenerMainDistanceChet: ListenerMainDistanceChet){
        val builderChet = AlertDialog.Builder(context)
        val binding = MainDistanceChetDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builderChet.setView(binding.root)
        val dialogChet = builderChet.create()
        binding.apply {
            dialogMainDistanceSaveChet.setOnClickListener {
                val dialogDistanceChet = binding.dialogMainDistanceChet.text.toString()
                dialogMainDistanceChet.setText(dialogDistanceChet)

                listenerMainDistanceChet.onClickMainDistanceChet(dialogDistanceChet)
                dialogChet.dismiss()
            }
            dialogMainDistanceCancelChet.setOnClickListener {
                dialogChet.dismiss()
            }
        }
        dialogChet.show()
    }

    fun showDialogMainUslChet(context: Context, listenerMainUslChet: ListenerMainUslChet){
        val builderChet = AlertDialog.Builder(context)
        val binding = MainUslChetBinding.inflate(LayoutInflater.from(context), null, false)
        builderChet.setView(binding.root)
        val dialogChet = builderChet.create()
        binding.apply {
            MainDialogUslSaveChet.setOnClickListener {
                val dialogUslChet = binding.mainDialogUslChet.text.toString()
                mainDialogUslChet.setText(dialogUslChet)

                listenerMainUslChet.onClickMainUslChet(dialogUslChet)
                dialogChet.dismiss()
            }
            MainDialogUslCancelChet.setOnClickListener {
                dialogChet.dismiss()
            }
        }
        dialogChet.show()
    }

    interface ListenerTrack{
        fun onClickTrackChet()
    }

    interface Listener{
        fun onClickChet()
    }

    interface ListenerMainDistanceChet{
        fun onClickMainDistanceChet(dialogDistanceChet: String)
    }

    interface ListenerMainUslChet{
        fun onClickMainUslChet(dialogUslChet: String)
    }
}