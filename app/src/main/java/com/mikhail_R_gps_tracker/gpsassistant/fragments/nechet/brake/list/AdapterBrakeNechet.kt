package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.brake.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ItemBrakeNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.ListItemBrakeNechet
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake

class AdapterBrakeNechet(listMain: ArrayList<ListItemBrakeNechet>, listBrakeFragmentNechet: ListBrakeFragmentNechet) : RecyclerView.Adapter<AdapterBrakeNechet.MyViewHolder>() {
    private var listArray = listMain
    private var listBrakeFragmentNechet = listBrakeFragmentNechet

    class MyViewHolder(view: View, context: ListBrakeFragmentNechet) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBrakeNechetBinding.bind(view)

        fun setData(item: ListItemBrakeNechet) = with(binding){
            kmStartItemBrakeNechet.text = item.startNechet.toString()
            pkStartItemBrakeNechet.text = item.picketStartNechet.toString()

            idItemLayoutBrakeNechet.setOnClickListener {
                val action = ListBrakeFragmentNechetDirections.actionListBrakeFragmentNechetToUpdateBrakeFragmentNechet(item)
                idItemLayoutBrakeNechet.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater.inflate(R.layout.item_brake_nechet, parent, false), listBrakeFragmentNechet)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(listArray[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateBrakeNechet(listItems: List<ListItemBrakeNechet>){
        listArray.clear()
        listArray.addAll(listItems)
        listArray.sortByDescending { it.picketStartNechet }
        listArray.sortByDescending { it.startNechet }
        notifyDataSetChanged()
    }

    fun removeItemBrakeNechet(pos: Int, dbManagerBrake: MyDbManagerBrake){
        dbManagerBrake.deleteDbDataBrakeNechet(listArray[pos].idNechet)
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}