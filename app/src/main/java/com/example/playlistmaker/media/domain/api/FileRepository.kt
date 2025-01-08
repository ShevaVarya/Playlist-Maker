package com.example.playlistmaker.media.domain.api

import android.net.Uri

interface FileRepository {
    fun getUriFromPath(path: String?): Uri
}