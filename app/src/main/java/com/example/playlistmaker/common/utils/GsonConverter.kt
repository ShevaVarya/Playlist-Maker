package com.example.playlistmaker.common.utils

import com.google.gson.Gson
import java.lang.reflect.Type

class GsonConverter {

    companion object {
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

        fun <T : Any> createItemFromJson(json: String, type: Class<T>): T {
            return Gson().fromJson(json, type)
        }
    }
}