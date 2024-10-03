package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import java.io.IOException

class RetrofitNetworkClient(private val service: ITunesApi) : NetworkClient {
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