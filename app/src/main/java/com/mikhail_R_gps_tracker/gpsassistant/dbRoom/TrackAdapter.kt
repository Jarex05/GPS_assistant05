package com.mikhail_R_gps_tracker.gpsassistant.dbRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.TrackItemBinding


class TrackAdapter(private val listener: Listener) : ListAdapter<TrackItem, TrackAdapter.Holder>(Comparatop()) {
    class Holder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view), View.OnClickListener{
        private val binding = TrackItemBinding.bind(view)
        private var trackTemp: TrackItem? = null
        init {
            binding.idSaveTrack.setOnClickListener(this)
            binding.ibDelete.setOnClickListener(this)
            binding.item.setOnClickListener(this)
        }
        fun bind(track: TrackItem) = with(binding){
            trackTemp = track
            val title = track.title
            val distance = "${track.distance} км"

            tvTitleTrack.text = title
            tvDistanceItem.text = distance

        }

        override fun onClick(view: View) {
            val type = when(view.id){
                R.id.idSaveTrack -> ClickType.SAVE
                R.id.ibDelete -> ClickType.DELETE
                R.id.item -> ClickType.OPEN
                else -> ClickType.OPEN
            }
            trackTemp?.let { listener.onClick(it, type) }
        }
    }

    class Comparatop : DiffUtil.ItemCallback<TrackItem>(){
        override fun areItemsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackItem, newItem: TrackItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onClick(track: TrackItem, type: ClickType)
    }

    enum class ClickType{
        SAVE,
        DELETE,
        OPEN
    }
}
