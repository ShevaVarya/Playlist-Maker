package com.example.playlistmaker.media.ui.playlists.createPlaylist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FileInteractor
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch
import java.util.UUID

open class CreatePlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val fileInteractor: FileInteractor,
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
            var imagePath: String? = null
            if (imageUri != Uri.EMPTY) {
                imagePath = fileInteractor.saveImageToPrivateStorage(playlistImage!!, imageUri)
            }
            interactor.createPlaylist(playlistName, playlistDescription, imagePath)

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
            var imagePath: String? = null
            if (imageUri != Uri.EMPTY) {
                imagePath = fileInteractor.saveImageToPrivateStorage(playlistImage!!, imageUri)
            }

            if (imageUri != Uri.EMPTY) {
                interactor.updatePlaylist(
                    playlist.value!!.copy(
                        playlistName = playlistName,
                        playlistDescription = playlistDescription,
                        imagePath = imagePath
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


        }
    }

    fun loadPlaylist(id: Int) {
        viewModelScope.launch {
            interactor.getPlaylistById(id).collect {
                playlist.postValue(it)
            }
        }
    }

}