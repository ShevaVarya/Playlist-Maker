package com.example.playlistmaker.media.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface OpeningAction : Parcelable {

    @Parcelize
    data object CreatePlaylist : OpeningAction

    @Parcelize
    data class UpdatePlaylist(val playlistId: Int) : OpeningAction
}