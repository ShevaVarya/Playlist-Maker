package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?
    )

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Playlist

    fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist): Flow<Playlist>

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(id: Int): Flow<Playlist>

    fun getPlaylistTracks(list: List<Int>): Flow<List<Track>>
}