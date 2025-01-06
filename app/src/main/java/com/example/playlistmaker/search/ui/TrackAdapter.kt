package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(
    private val onItemClickListener: OnItemClickListener<Track>,
    private val onItemLongListener: OnItemClickListener<Track>? = null
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(tracks[position])
        }
        if (onItemLongListener != null) {
            holder.itemView.setOnLongClickListener {
                onItemLongListener.onClick(tracks[position])
                true
            }
        }
    }
}