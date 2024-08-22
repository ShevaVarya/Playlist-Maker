package com.example.playlistmaker.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.common.util.Formatter

class TrackViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    private val trackName = view.findViewById<TextView>(R.id.track_name)
    private val artistName = view.findViewById<TextView>(R.id.track_artist)
    private val trackTime = view.findViewById<TextView>(R.id.track_time)
    private val artwork = view.findViewById<ImageView>(R.id.track_image)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTimeMillis
        Glide.with(view)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(Formatter.dpToPx(2f, view.context)))
            .placeholder(R.drawable.placeholder)
            .into(artwork)
    }


}