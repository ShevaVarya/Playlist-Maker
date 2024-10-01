package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(ITunesApi::class.java)

    override fun makeRequest(dto: Any): Response {
        if (dto is TrackSearchRequest) {
            try {
                val response = service.searchTrack(dto.text).execute()
                val body = response.body() ?: Response()
                return body.apply {
                    resultCode = response.code()
                }
            } catch (e: IOException) {
                return Response().apply { resultCode = 503 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}