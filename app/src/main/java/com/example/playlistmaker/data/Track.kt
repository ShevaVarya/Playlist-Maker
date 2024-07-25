package com.example.playlistmaker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (
    val trackId: Int, //ID трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?, // Название альбома
    val releaseDate: String, // Дата релиза
    val primaryGenreName: String, // Жанр трека
    val country: String // Страна исполнителя
    ): Parcelable {

    override fun equals(other: Any?): Boolean {
        return (other as Track).trackId == trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}