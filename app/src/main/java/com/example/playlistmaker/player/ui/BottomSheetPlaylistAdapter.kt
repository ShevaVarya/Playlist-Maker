package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.ui.OnItemClickListener

class BottomSheetPlaylistAdapter(
    private val onItemClickListener: OnItemClickListener<Playlist>
) : RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {

    val playlistList = ArrayList<Playlist>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistViewHolder {
        val binding = ItemPlaylistBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomSheetPlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int = playlistList.size

    override fun onBindViewHolder(holder: BottomSheetPlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(playlistList[position])
        }
    }


}