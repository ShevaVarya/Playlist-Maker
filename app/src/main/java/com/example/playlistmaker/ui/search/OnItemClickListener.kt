package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.models.Track

fun interface OnItemClickListener {
    fun onClick(item: Track)
}