package com.example.playlistmaker.media.data.db.converters

import com.example.playlistmaker.common.utils.GsonConverter
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            listTracks = GsonConverter.createJsonFromList(playlist.listTracksId),
            countTrack = playlist.countTracks
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type
        return Playlist(
            playlistId = playlistEntity.id,
            playlistName = playlistEntity.playlistName,
            playlistDescription = playlistEntity.playlistDescription,
            imagePath = playlistEntity.imagePath,
            listTracksId = ArrayList(
                GsonConverter.createListFromJson<Int>(
                    playlistEntity.listTracks,
                    type
                )
            ),
            countTracks = playlistEntity.countTrack
        )
    }
}