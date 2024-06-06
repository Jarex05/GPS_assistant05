package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.redacktor.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ItemRedacktorNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.ListItemRedacktorNechet
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor

class AdapterRedacktorNechet(listMain: ArrayList<ListItemRedacktorNechet>, listRedacktorFragmentNechet: ListRedacktorFragmentNechet) : RecyclerView.Adapter<AdapterRedacktorNechet.MyViewHolder>() {
    private var listArray = listMain
    private var listRedacktorFragmentNechet = listRedacktorFragmentNechet

    class MyViewHolder(view: View, context: ListRedacktorFragmentNechet) : RecyclerView.ViewHolder(view) {
        private val binding = ItemRedacktorNechetBinding.bind(view)

        fun setData(item: ListItemRedacktorNechet) = with(binding){
            kmItemRedacktorNechet.text = item.startNechet.toString()
            pkItemRedacktorNechet.text = item.picketStartNechet.toString()
            minusItemRedactorNechet.text = item.minusNechet
            plusItemRedacktorNechet.text = item.plusNechet

            idItemLayoutRedacktorNechet.setOnClickListener {
                val action = ListRedacktorFragmentNechetDirections.actionListRedacktorFragmentNechetToUpdateRedacktorFragmentNechet(item)
                idItemLayoutRedacktorNechet.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return  MyViewHolder(inflater.inflate(R.layout.item_redacktor_nechet, parent, false), listRedacktorFragmentNechet)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(listArray[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterNechet(listItems: List<ListItemRedacktorNechet>){
        listArray.clear()
        listArray.addAll(listItems)
        listArray.sortByDescending { it.picketStartNechet }
        listArray.sortByDescending { it.startNechet }
        notifyDataSetChanged()
    }

    fun removeItemNechet(pos: Int, dbManagerRedacktor: MyDbManagerRedacktor){
        dbManagerRedacktor.deleteDbDataRedactorNechet(listArray[pos].idNechet)
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}