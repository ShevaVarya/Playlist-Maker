package com.example.playlistmaker.presentation.ui.search

import com.example.playlistmaker.domain.models.Track

fun interface OnItemClickListener {
    fun onClick(item: Track)
}