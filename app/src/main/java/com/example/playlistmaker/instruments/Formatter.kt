package com.example.playlistmaker.instruments

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

class Formatter {
    companion object {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            ).toInt()
        }

        fun msToMinute(sec: Long): String {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(sec)
        }
    }
}