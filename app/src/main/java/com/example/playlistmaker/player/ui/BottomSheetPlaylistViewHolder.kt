package com.example.playlistmaker.player.ui

import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.common.utils.getCacheImagePath
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.media.domain.models.Playlist
import java.io.File

class BottomSheetPlaylistViewHolder(
    private val binding: ItemPlaylistBottomSheetBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist) {
        binding.bottomSheetPlaylistName.text = model.playlistName
        binding.bottomSheetPlaylistCount.text = itemView.resources.getQuantityString(
            R.plurals.track_count,
            model.countTracks,
            model.countTracks
        )

        val filePath = getCacheImagePath(itemView.context)
        val file = File(filePath, model.imagePath ?: "")
        Glide.with(itemView)
            .load(file.toUri())
            .placeholder(R.drawable.placeholder)
            .apply(
                RequestOptions().transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(Formatter.dpToPx(8f, itemView.context))
                    )
                )
            )
            .into(binding.bottomSheetPlaylistImage)
    }

}