package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converters.PlaylistDbConverter
import com.example.playlistmaker.media.data.db.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter,
) : PlaylistRepository {
    override suspend fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        playlistImage: String?
    ) {
        val playlistEntity = PlaylistEntity(
            playlistName = playlistName,
            playlistDescription = playlistDescription,
            imagePath = playlistImage,
            listTracks = "",
            countTrack = 0
        )
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().deletePlaylist(playlist.playlistId)
        playlist.listTracksId.forEach { trackId ->
            if (!checkTrack(trackId)) appDatabase.playlistTrackDao().deleteTrack(trackId)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        appDatabase.playlistTrackDao().insertTrack(playlistTrackDbConverter.map(track))
        appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
    }

    override fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist): Flow<Playlist> = flow {
        playlist.listTracksId.remove(trackId)

        val newPlaylist = playlist.copy(countTracks = playlist.countTracks - 1)
        updatePlaylist(newPlaylist)

        if (!checkTrack(trackId)) appDatabase.playlistTrackDao().deleteTrack(trackId)

        emit(newPlaylist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylist()
        emit(convertFromListPlaylistEntity(playlists))
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getPlaylistById(id)
        emit(playlistDbConverter.map(playlist))
    }

    override fun getPlaylistTracks(listId: List<Int>): Flow<List<Track>> = flow {
        val result = appDatabase.playlistTrackDao().getAllTracks(listId).map {
            playlistTrackDbConverter.map(it)
        }
        val trackMap = result.associateBy { it.trackId }
        emit(listId.reversed().mapNotNull { trackMap[it] })

    }

    private fun convertFromListPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private suspend fun checkTrack(trackId: Int): Boolean {
        val list = ArrayList<Playlist>()
        getPlaylists().collect {
            list.addAll(it)
        }
        for (playlist in list) {
            if (playlist.listTracksId.contains(trackId))
                return true
        }
        return false
    }

}