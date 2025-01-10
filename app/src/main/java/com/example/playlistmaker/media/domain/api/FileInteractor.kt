package com.example.playlistmaker.media.domain.api

import android.net.Uri

interface FileInteractor {
    fun saveImageToPrivateStorage(fileName: String, imageUri: Uri): String
}