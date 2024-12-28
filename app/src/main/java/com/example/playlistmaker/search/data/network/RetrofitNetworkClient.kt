package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class RetrofitNetworkClient(private val service: ITunesApi) : NetworkClient {
    override suspend fun makeRequest(dto: Any): Response {
        return withContext(Dispatchers.IO) {
            if (dto is TrackSearchRequest) {
                try {
                    val response = service.searchTrack(dto.text)

                    response.apply {
                        resultCode = 200
                    }
                } catch (e: IOException) {
                    Response().apply { resultCode = 503 }
                }
            } else {
                Response().apply { resultCode = 400 }
            }
        }
    }
}