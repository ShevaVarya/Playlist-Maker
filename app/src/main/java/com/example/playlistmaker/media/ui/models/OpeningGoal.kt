package com.example.playlistmaker.media.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface OpeningGoal : Parcelable {

    @Parcelize
    data object CreatePlaylist : OpeningGoal

    @Parcelize
    data class UpdatePlaylist(val playlistId: Int) : OpeningGoal
}