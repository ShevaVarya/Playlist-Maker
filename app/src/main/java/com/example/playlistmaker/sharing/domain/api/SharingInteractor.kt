package com.example.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun sendEmail()
    fun openBrowser()
    fun sendMessage()
}