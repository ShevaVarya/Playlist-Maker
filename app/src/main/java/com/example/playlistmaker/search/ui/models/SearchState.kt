package com.example.playlistmaker.search.ui.models

import android.icu.text.CaseMap.Title
import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Loading : SearchState

    object EmptyHistory : SearchState

    data class ContentHistory(
        val tracks: List<Track>
    ) : SearchState

    data class ContentSearch(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorMessageTitle: String,
        val errorMessageSubtitle: String
    ) : SearchState

    data class NotFound(
        val message: String
    ) : SearchState
}
