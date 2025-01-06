package com.example.playlistmaker.common.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import java.io.File

object Utils {
    const val SHARED_PREFERENCES_NAME_FILE = "SharedPreferencePlaylistMaker"
    const val SHARED_PREFERENCES_KEY_THEME = "shared_preferences_key"
    const val MAX_SIZES_SEARCH_HISTORY = 10
    const val TRACK_EXTRA = "TRACK"
}

fun getCacheImagePath(context: Context): File =
    File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")

inline fun <reified T> getExtraWithVersions(intent: Intent): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(Utils.TRACK_EXTRA, T::class.java)!!
    } else {
        @Suppress("DEPRECATION")
        intent.getParcelableExtra(Utils.TRACK_EXTRA)!!
    }
}