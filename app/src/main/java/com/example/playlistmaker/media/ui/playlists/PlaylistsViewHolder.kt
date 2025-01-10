package com.example.playlistmaker.media.ui.playlists

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistsViewHolder(
    val binding: ItemPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        binding.playlistName.text = model.playlistName
        binding.playlistCount.text = itemView.resources.getQuantityString(
            R.plurals.track_count,
            model.countTracks,
            model.countTracks
        )

        Glide.with(itemView)
            .load(model.imagePath ?: "")
            .placeholder(R.drawable.placeholder)
            .apply(
                RequestOptions().transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(Formatter.dpToPx(8f, itemView.context))
                    )
                )
            )
            .into(binding.playlistImage)
    }

}