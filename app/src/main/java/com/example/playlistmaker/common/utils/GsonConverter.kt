package com.example.playlistmaker.common.utils

import com.google.gson.Gson
import java.lang.reflect.Type

class GsonConverter(
    private val gson: Gson
) {

    fun <T> createJsonFromList(list: List<T>): String {
        return gson.toJson(list)
    }

    fun <T> createListFromJson(json: String?, itemType: Type): List<T> {
        if (json.isNullOrEmpty()) {
            return emptyList()
        } else {
            return gson.fromJson(json, itemType)
        }
    }
}