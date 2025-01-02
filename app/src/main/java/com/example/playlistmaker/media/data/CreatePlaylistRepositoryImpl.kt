package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.domain.api.CreatePlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class CreatePlaylistRepositoryImpl() : CreatePlaylistRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        TODO("Not yet implemented")
    }

}