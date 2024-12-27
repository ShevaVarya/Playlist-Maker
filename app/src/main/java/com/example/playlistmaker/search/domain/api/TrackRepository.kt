package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.common.utils.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTrack(text: String): Flow<Resource<List<Track>>>
}