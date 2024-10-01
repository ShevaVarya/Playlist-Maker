package com.example.playlistmaker.search.domain.models

data class Track(
    val trackId: Int, //ID трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String?, // Название альбома
    val releaseDate: String, // Дата релиза
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val previewUrl: String //Ссылка на отрывок трека
)