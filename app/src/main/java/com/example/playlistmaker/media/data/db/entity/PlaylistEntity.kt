package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, //ID в базе данных
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String?,
    val listTracks: String = "",
    val countTrack: Int,
)