package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TrackRepository {
    fun searchTrack(text: String): Pair<Int,List<Track>>
}