package com.example.playlistmaker.media.ui.models

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistState {

    data object EmptyPlaylists : PlaylistState

    data class Content(
        val playlists: List<Playlist>
    ): PlaylistState
}