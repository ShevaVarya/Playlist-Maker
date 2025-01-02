package com.example.playlistmaker.media.domain.implementation

import com.example.playlistmaker.media.domain.api.CreatePlaylistInteractor
import com.example.playlistmaker.media.domain.api.CreatePlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class CreatePlaylistInteractorImpl(
    private val repository: CreatePlaylistRepository
) : CreatePlaylistInteractor {

    override suspend fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?
    ) {
        repository.createPlaylist(playlistName, playlistDescription, playlistImage)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }
}