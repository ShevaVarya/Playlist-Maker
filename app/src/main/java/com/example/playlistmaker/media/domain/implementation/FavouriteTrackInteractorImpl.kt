package com.example.playlistmaker.media.domain.implementation

import com.example.playlistmaker.media.domain.api.FavouriteTrackInteractor
import com.example.playlistmaker.media.domain.api.FavouriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteTrackInteractorImpl(
    private val repository: FavouriteTracksRepository
) : FavouriteTrackInteractor {
    override suspend fun addTrackToFavourite(track: Track) {
        repository.addTrackToFavourite(track)
    }

    override suspend fun deleteTrackFromFavourite(track: Track) {
        repository.deleteTrackFromFavourite(track)
    }

    override fun getFavouriteTracks(): Flow<List<Track>> {
        return repository.getFavouriteTracks().map { tracks ->
            tracks.reversed()
        }
    }
}