package com.example.playlistmaker.common.utils

import android.content.Context
import android.os.Environment
import java.io.File

object Utils {
    const val SHARED_PREFERENCES_NAME_FILE = "SharedPreferencePlaylistMaker"
    const val SHARED_PREFERENCES_KEY_THEME = "shared_preferences_key"
    const val MAX_SIZES_SEARCH_HISTORY = 10
}

fun getCacheImagePath(context: Context): File =
    File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "cache")