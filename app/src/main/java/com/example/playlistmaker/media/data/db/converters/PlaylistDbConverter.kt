package com.example.playlistmaker.media.data.db.converters

import com.example.playlistmaker.common.utils.GsonConverter
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            imagePath = playlist.imagePath,
            listTracks = GsonConverter.createJsonFromList(playlist.listTracksId),
            countTrack = playlist.countTracks
        )
    }

   fun map(playlistEntity: PlaylistEntity): Playlist {
       val type = object : TypeToken<List<Playlist>>() {}.type
       return Playlist(
           playlistId = playlistEntity.id,
           playlistName = playlistEntity.playlistName,
           playlistDescription = playlistEntity.playlistDescription,
           imagePath = playlistEntity.imagePath,
           listTracksId = GsonConverter.createListFromJson<Int>(playlistEntity.listTracks, type),
           countTracks = playlistEntity.countTrack
       )
   }
}