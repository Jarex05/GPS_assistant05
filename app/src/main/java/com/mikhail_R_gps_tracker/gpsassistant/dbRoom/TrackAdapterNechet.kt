package com.mikhail_R_gps_tracker.gpsassistant.dbRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.TrackItemNechetBinding

class TrackAdapterNechet(private val listenerNechet: ListenerNechet) : ListAdapter<TrackItemNechet, TrackAdapterNechet.HolderNechet>(Comparatop()) {
    class HolderNechet(view: View, private val listenerNechet: ListenerNechet) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private val binding = TrackItemNechetBinding.bind(view)
        private var trackTempNechet: TrackItemNechet? = null
        init {
            binding.idSaveTrackNechet.setOnClickListener(this)
            binding.ibDeleteNechet.setOnClickListener(this)
            binding.itemNechet.setOnClickListener(this)
        }
        fun bind(track: TrackItemNechet) = with(binding){
            trackTempNechet = track
            val title = track.titleNechet
            val distance = "${track.distanceNechet} км"

            tvTitleTrackNechet.text = title
            tvDistanceItemNechet.text = distance

        }

        override fun onClick(view: View) {
            val type = when(view.id){
                R.id.idSaveTrackNechet -> ClickTypeNechet.SAVE
                R.id.ibDeleteNechet -> ClickTypeNechet.DELETE
                R.id.itemNechet -> ClickTypeNechet.OPEN
                else -> ClickTypeNechet.OPEN
            }
            trackTempNechet?.let { listenerNechet.onClickNechet(it, type) }
        }
    }

    class Comparatop : DiffUtil.ItemCallback<TrackItemNechet>(){
        override fun areItemsTheSame(oldItem: TrackItemNechet, newItem: TrackItemNechet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItemNechet, newItem: TrackItemNechet): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderNechet {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item_nechet, parent, false)
        return HolderNechet(view, listenerNechet)
    }

    override fun onBindViewHolder(holder: HolderNechet, position: Int) {
        holder.bind(getItem(position))
    }

    interface ListenerNechet{
        fun onClickNechet(track: TrackItemNechet, type: ClickTypeNechet)
    }

    enum class ClickTypeNechet{
        SAVE,
        DELETE,
        OPEN
    }
}