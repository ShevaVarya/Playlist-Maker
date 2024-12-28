package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.converters.TrackDbConverter
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.media.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavouriteTracksRepository {
    override suspend fun addTrackToFavourite(track: Track) {
        appDatabase.trackDao().insertTrack(
            trackDbConverter.map(track)
        )
    }

    override suspend fun deleteTrackFromFavourite(track: Track) {
        appDatabase.trackDao().deleteTrack(
            trackDbConverter.map(track)
        )
    }

    override fun getFavouriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }
}