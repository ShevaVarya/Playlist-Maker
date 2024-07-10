package com.example.playlistmaker.trackRecyclerView

import com.example.playlistmaker.data.Track

fun interface OnItemClickListener {
    fun onClick(item: Track)
}