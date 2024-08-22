package com.example.playlistmaker.data

import com.example.playlistmaker.common.util.Formatter
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImplementation(
    private val networkClient: NetworkClient
) : TrackRepository {
    override fun searchTrack(text: String): Pair<Int, List<Track>> {
        val response = networkClient.makeRequest(TrackSearchRequest(text))
        if (response.resultCode == 200) {
            return Pair(response.resultCode, (response as TrackSearchResponse).results.map {
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
            })
        } else {
            return Pair(response.resultCode, emptyList())
        }
    }
}