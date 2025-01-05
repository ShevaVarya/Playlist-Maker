package com.example.playlistmaker.common.utils

import com.google.gson.Gson
import java.lang.reflect.Type

class GsonConverter {

    fun <T> createJsonFromList(list: List<T>): String {
        return Gson().toJson(list)
    }

    fun <T> createListFromJson(json: String?, itemType: Type): List<T> {
        if (json.isNullOrEmpty()) {
            return emptyList()
        } else {
            return Gson().fromJson(json, itemType)

        }
    }
}