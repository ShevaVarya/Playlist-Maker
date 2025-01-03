package com.example.playlistmaker.media.domain.models

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String?,
    val listTracksId: ArrayList<Int>,
    var countTracks: Int
)