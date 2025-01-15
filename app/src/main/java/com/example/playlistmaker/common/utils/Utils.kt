package com.example.playlistmaker.common.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import java.io.File

object Utils {
    const val SHARED_PREFERENCES_NAME_FILE = "SharedPreferencePlaylistMaker"
    const val SHARED_PREFERENCES_KEY_THEME = "shared_preferences_key"
    const val MAX_SIZES_SEARCH_HISTORY = 10

}

fun getCacheImagePath(context: Context): File =
    File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")

inline fun <reified T : Parcelable> getParcelableCompat(
    source: Any, key: String
): T? {
    return when (source) {
        is Bundle -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            source.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            source.getParcelable(key)
        }
        is Intent -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            source.getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            source.getParcelableExtra(key)
        }
        else -> throw IllegalArgumentException("Unsupported source type")
    }
}
