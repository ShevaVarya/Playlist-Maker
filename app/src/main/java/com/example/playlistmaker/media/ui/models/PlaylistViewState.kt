package com.example.playlistmaker.media.ui.models

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistViewState {

    data object Empty : PlaylistViewState

    data class PlaylistUIModel(
        val playlist: Playlist,
        val list: List<Track>,
        val duration: String,
    ) : PlaylistViewState
}