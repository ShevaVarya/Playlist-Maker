package com.example.playlistmaker.media.ui.models

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

data class PlaylistUIModel (
    val playlist: Playlist,
    val list: List<Track>,
    val duration: String
)
    /*data class PlaylistUI(val playlist: Playlist): PlaylistUIModel
    data class Tracks(val list: List<Track>): PlaylistUIModel
    data class Duration(val duration: String): PlaylistUIModel
*/