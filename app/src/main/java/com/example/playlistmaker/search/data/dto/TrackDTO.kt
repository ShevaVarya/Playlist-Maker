package com.example.playlistmaker.search.data.dto

data class TrackDTO(
    val trackId: Int, //ID трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?, // Название альбома
    val releaseDate: String, // Дата релиза
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val previewUrl: String //Ссылка на отрывок трека
) {

}