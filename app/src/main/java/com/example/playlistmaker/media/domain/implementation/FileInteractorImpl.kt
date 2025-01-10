package com.example.playlistmaker.media.domain.implementation

import android.net.Uri
import com.example.playlistmaker.media.domain.api.FileInteractor
import com.example.playlistmaker.media.domain.api.FileRepository

class FileInteractorImpl(
    private val repository: FileRepository
) : FileInteractor {

    override fun saveImageToPrivateStorage(fileName: String, imageUri: Uri): String {
        return repository.saveImageToPrivateStorage(fileName, imageUri)
    }
}