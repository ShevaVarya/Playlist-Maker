package com.example.playlistmaker.media.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.example.playlistmaker.common.utils.getCacheImagePath
import com.example.playlistmaker.media.domain.api.FileRepository
import java.io.File

class FileRepositoryImpl(
    private val context: Context
) : FileRepository {

    override fun getUriFromPath(path: String?): Uri {
        val file = File(getCacheImagePath(context), path ?: "").toUri()
        return file
    }
}