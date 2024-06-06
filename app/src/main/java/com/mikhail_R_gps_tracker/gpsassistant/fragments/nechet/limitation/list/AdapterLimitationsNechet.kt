package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.limitation.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ItemLimitationsNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.ListItemLimitationsNechet
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations

class AdapterLimitationsNechet(listMain: ArrayList<ListItemLimitationsNechet>, listLimitationsFragmentNechet: ListLimitationsFragmentNechet) : RecyclerView.Adapter<AdapterLimitationsNechet.MyViewHolder>() {
    private var listArray = listMain
    private var listLimitationsFragmentNechet = listLimitationsFragmentNechet

    class MyViewHolder(view: View, context: ListLimitationsFragmentNechet) : RecyclerView.ViewHolder(view) {
        private val binding = ItemLimitationsNechetBinding.bind(view)

        fun setData(item: ListItemLimitationsNechet) = with(binding){
            kmStartItemLimitationsNechet.text = item.startNechet.toString()
            pkStartItemLimitationsNechet.text = item.picketStartNechet.toString()
            kmFinishItemLimitationsNechet.text = item.finishNechet.toString()
            pkFinishItemLimitationsNechet.text = item.picketFinishNechet.toString()
            speedItemLimitationsNechet.text = item.speedNechet.toString()

            idItemLayoutLimitationsNechet.setOnClickListener {
                val action = ListLimitationsFragmentNechetDirections.actionListLimitationsFragmentNechetToUpdateLimitationsFragmentNechet(item)
                idItemLayoutLimitationsNechet.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater.inflate(R.layout.item_limitations_nechet, parent, false), listLimitationsFragmentNechet)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(listArray[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLimitationsNechet(listItems: List<ListItemLimitationsNechet>){
        listArray.clear()
        listArray.addAll(listItems)
        listArray.sortBy { it.speedNechet }
        listArray.sortByDescending { it.picketStartNechet }
        listArray.sortByDescending { it.startNechet }
        notifyDataSetChanged()
    }

    fun removeItemLimitationsNechet(pos: Int, dbManagerLimitations: MyDbManagerLimitations){
        dbManagerLimitations.deleteDbDataLimitationsNechet(listArray[pos].idNechet)
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}