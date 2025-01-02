package com.example.playlistmaker.media.ui.models

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface CreatePlaylistState {

    data object EmptyPlaylists : CreatePlaylistState

    data class Content(
        val playlists: List<Playlist>
    ): CreatePlaylistState
}