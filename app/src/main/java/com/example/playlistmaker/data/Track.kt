package com.example.playlistmaker.data

data class Track (
    val trackId: Int, //ID трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
    ) {

    override fun equals(other: Any?): Boolean {
        return (other as Track).trackId == trackId
    }

    override fun hashCode(): Int {
        return trackId.hashCode()
    }
}