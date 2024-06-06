package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.brake.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ItemBrakeChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.ListItemBrakeChet
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake

class AdapterBrakeChet(listMain: ArrayList<ListItemBrakeChet>, listBrakeFragmentChet: ListBrakeFragmentChet) : RecyclerView.Adapter<AdapterBrakeChet.MyViewHolder>() {
    private var listArray = listMain
    private var listBrakeFragmentChet = listBrakeFragmentChet

    class MyViewHolder(view: View, context: ListBrakeFragmentChet) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBrakeChetBinding.bind(view)

        fun setData(item: ListItemBrakeChet) = with(binding){
            kmStartItemBrakeChet.text = item.startChet.toString()
            pkStartItemBrakeChet.text = item.picketStartChet.toString()

            idItemLayoutBrakeChet.setOnClickListener {
                val action = ListBrakeFragmentChetDirections.actionListBrakeFragmentChetToUpdateBrakeFragmentChet(item)
                idItemLayoutBrakeChet.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater.inflate(R.layout.item_brake_chet, parent, false), listBrakeFragmentChet)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(listArray[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateBrakeChet(listItems: List<ListItemBrakeChet>){
        listArray.clear()
        listArray.addAll(listItems)
        listArray.sortBy { it.picketStartChet }
        listArray.sortBy { it.startChet }
        notifyDataSetChanged()
    }

    fun removeItemBrakeChet(pos: Int, dbManagerBrake: MyDbManagerBrake){
        dbManagerBrake.deleteDbDataBrakeChet(listArray[pos].idChet)
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}