package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.limitation.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ItemLimitationsChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.ListItemLimitationsChet
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations

class AdapterLimitationsChet(listMain: ArrayList<ListItemLimitationsChet>, listLimitationsFragmentChet: ListLimitationsFragmentChet) : RecyclerView.Adapter<AdapterLimitationsChet.MyViewHolder>() {
    private var listArray = listMain
    private var listLimitationsFragmentChet = listLimitationsFragmentChet

    inner class MyViewHolder(view: View, context: ListLimitationsFragmentChet) : RecyclerView.ViewHolder(view) {
        private val binding = ItemLimitationsChetBinding.bind(view)

        fun setData(item: ListItemLimitationsChet) = with(binding){
            kmStartItemLimitationsChet.text = item.startChet.toString()
            pkStartItemLimitationsChet.text = item.picketStartChet.toString()
            kmFinishItemLimitationsChet.text = item.finishChet.toString()
            pkFinishItemLimitationsChet.text = item.picketFinishChet.toString()
            speedItemLimitationsChet.text = item.speedChet.toString()

            idItemLayoutLimitationsChet.setOnClickListener {
                val action = ListLimitationsFragmentChetDirections.actionListLimitationsFragmentChetToUpdateLimitationsFragmentChet(item)
                idItemLayoutLimitationsChet.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater.inflate(R.layout.item_limitations_chet, parent, false), listLimitationsFragmentChet)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(listArray[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLimitationsChet(listItems: List<ListItemLimitationsChet>){
        listArray.clear()
        listArray.addAll(listItems)
        listArray.sortBy { it.speedChet }
        listArray.sortBy { it.picketStartChet }
        listArray.sortBy { it.startChet }
        notifyDataSetChanged()
    }

    fun removeItemLimitationsChet(pos: Int, dbManagerLimitations: MyDbManagerLimitations){
        dbManagerLimitations.deleteDbDataLimitationsChet(listArray[pos].idChet)
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}