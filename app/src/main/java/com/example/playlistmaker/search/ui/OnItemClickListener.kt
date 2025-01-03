package com.example.playlistmaker.search.ui

fun interface OnItemClickListener<T> {
    fun onClick(item: T)
}