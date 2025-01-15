package com.example.playlistmaker.media.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.playlistmaker.common.utils.getCacheImagePath
import com.example.playlistmaker.media.domain.api.FileRepository
import java.io.File
import java.io.FileOutputStream

class FileRepositoryImpl(
    private val context: Context
) : FileRepository {

    override fun saveImageToPrivateStorage(fileName: String, imageUri: Uri): String {
        val filePath = getCacheImagePath(context)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, fileName)

        val inputStream = context.contentResolver.openInputStream(imageUri)

        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.toString()
    }
}