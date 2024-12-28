package com.example.playlistmaker.search.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.common.utils.Resource
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.domain.api.TrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val application: Context
) : TrackRepository {
    override fun searchTrack(text: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.makeRequest(TrackSearchRequest(text))

        when (response.resultCode) {
            200 -> {
                with(response as TrackSearchResponse) {
                    val data = results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            Formatter.msToMinute(it.trackTimeMillis),
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error(application.getString(R.string.connection_problem)))
            }
        }
    }
}