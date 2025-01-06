package com.example.playlistmaker.media.domain.implementation

import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

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

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Playlist {
        playlist.listTracksId.add(track.trackId)
        val newPlaylist = playlist.copy(countTracks = playlist.countTracks + 1)
        repository.addTrackToPlaylist(track, newPlaylist)
        return newPlaylist
    }

    override fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist): Flow<Playlist> {
        return repository.deleteTrackFromPlaylist(trackId, playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return repository.getPlaylistById(id)
    }

    override fun getPlaylistTracks(list: List<Int>): Flow<List<Track>> {
        return repository.getPlaylistTracks(list)
    }
}