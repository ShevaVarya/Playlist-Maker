package com.example.playlistmaker.media.ui.playlists

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.utils.getCacheImagePath
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CreatePlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val application: Context,
) : ViewModel() {

    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        imageUri: Uri,
        bitmap: Bitmap
    ) {
        var playlistImage: String? = null
        if (imageUri != Uri.EMPTY)
            playlistImage = "${UUID.randomUUID()}.png"
        viewModelScope.launch {
            interactor.createPlaylist(playlistName, playlistDescription, playlistImage)
            if (imageUri != Uri.EMPTY) {
                saveImageToPrivateStorage(bitmap, playlistImage!!)
            }
        }
    }

    private fun saveImageToPrivateStorage(bitmap: Bitmap, fileName: String) {
        val filePath = getCacheImagePath(application)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, fileName)

        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        bitmap
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

}