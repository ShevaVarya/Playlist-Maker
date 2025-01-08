package com.example.playlistmaker.media.domain.api

import android.net.Uri

interface FileInteractor {
    fun getUriFromPath(path: String?): Uri
}