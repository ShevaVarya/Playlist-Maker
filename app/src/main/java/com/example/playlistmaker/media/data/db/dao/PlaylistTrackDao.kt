package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table WHERE trackId IN (:list)")
    suspend fun getAllTracks(list: List<Int>): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_track_table WHERE trackId = :id")
    suspend fun deleteTrack(id: Int)
}