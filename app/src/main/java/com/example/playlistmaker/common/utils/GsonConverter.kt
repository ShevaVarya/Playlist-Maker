package com.example.playlistmaker.common.utils

import com.google.gson.Gson
import java.lang.reflect.Type

class GsonConverter {

    companion object {
        fun <T> createJsonFromList(list: List<T>): String {
            return Gson().toJson(list)
        }

        fun <T> createListFromJson(json: String?, itemType: Type): List<T> {
            if (json != null) {
                return Gson().fromJson(json, itemType)
            } else {
                return emptyList()
            }
        }
    }
}