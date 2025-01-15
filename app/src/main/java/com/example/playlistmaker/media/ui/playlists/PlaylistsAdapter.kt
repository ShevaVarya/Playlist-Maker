package com.example.playlistmaker.media.ui.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.ui.OnItemClickListener

class PlaylistsAdapter(
    private val onItemClickListener: OnItemClickListener<Playlist>
) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistsViewHolder(binding)
    }

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(playlists[position])
        }
    }
}