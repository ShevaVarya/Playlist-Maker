package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(playlistName: String, playlistDescription: String, playlistImage: String?)

    suspend fun deletePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun updatePlaylist(playlist: Playlist)
}