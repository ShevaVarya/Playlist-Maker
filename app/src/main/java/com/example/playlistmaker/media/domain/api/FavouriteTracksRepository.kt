package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTracksRepository {

    suspend fun addTrackToFavourite(track: Track)

    suspend fun deleteTrackFromFavourite(track: Track)

    fun getFavouriteTracks(): Flow<List<Track>>

}