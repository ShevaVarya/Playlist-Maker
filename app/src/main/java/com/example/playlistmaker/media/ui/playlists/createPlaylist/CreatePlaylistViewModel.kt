package com.example.playlistmaker.media.ui.playlists.createPlaylist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.common.utils.getCacheImagePath
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

open class CreatePlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val application: Context,
) : ViewModel() {

    private val playlist = MutableLiveData<Playlist>()
    fun getPlaylist(): LiveData<Playlist> = playlist

    fun createPlaylist(
        playlistName: String,
        playlistDescription: String,
        imageUri: Uri,
    ) {
        var playlistImage: String? = null
        if (imageUri != Uri.EMPTY)
            playlistImage = "${UUID.randomUUID()}.png"
        viewModelScope.launch {
            interactor.createPlaylist(playlistName, playlistDescription, playlistImage)
            if (imageUri != Uri.EMPTY) {
                saveImageToPrivateStorage(playlistImage!!, imageUri)
            }
        }
    }

    fun updatePlaylist(
        playlistName: String,
        playlistDescription: String,
        imageUri: Uri,
    ) {
        var playlistImage: String? = null
        if (imageUri != Uri.EMPTY)
            playlistImage = "${UUID.randomUUID()}.png"
        viewModelScope.launch {
            if (imageUri != Uri.EMPTY) {
                interactor.updatePlaylist(
                    playlist.value!!.copy(
                        playlistName = playlistName,
                        playlistDescription = playlistDescription,
                        imagePath = playlistImage
                    )
                )
            } else {
                interactor.updatePlaylist(
                    playlist.value!!.copy(
                        playlistName = playlistName,
                        playlistDescription = playlistDescription,
                        imagePath = playlist.value!!.imagePath
                    )
                )
            }

            if (imageUri != Uri.EMPTY) {
                saveImageToPrivateStorage(playlistImage!!, imageUri)
            }
        }
    }

    fun loadPlaylist(id: Int) {
        viewModelScope.launch {
            interactor.getPlaylistById(id).collect {
                playlist.postValue(it)
            }
        }
    }

    private fun saveImageToPrivateStorage(fileName: String, imageUri: Uri) {
        val filePath = getCacheImagePath(application)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, fileName)

        val inputStream = application.contentResolver.openInputStream(imageUri)

        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

}