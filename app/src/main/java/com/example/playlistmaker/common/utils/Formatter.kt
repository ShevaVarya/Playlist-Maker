package com.example.playlistmaker.common.utils

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

class Formatter {
    companion object {
        fun dpToPx(dp: Float, context: Context): Int =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()

        fun msToMinute(sec: Long): String =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(sec)

        fun getCoverArtwork(artworkUrl100: String) =
            artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        fun getYear(year: String) = year.substring(0, 4)

        fun formatTracks(count: Int): String {
            return when {
                count % 10 == 1 && count % 100 != 11 -> "$count трек"
                count % 10 in 2..4 && (count % 100 !in 12..14) -> "$count трека"
                else -> "$count треков"
            }
        }
    }
}