package com.example.playlistmaker.media.ui.favourite.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavouriteState {

    data object EmptyFavourite : FavouriteState

    data class ContentSearch(
        val tracks: List<Track>
    ) : FavouriteState
}