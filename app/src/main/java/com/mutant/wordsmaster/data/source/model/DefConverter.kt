package com.mutant.wordsmaster.data.source.model

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DefConverter {

    companion object {

        @JvmStatic
        @TypeConverter
        fun toDefs(gsonStr: String): List<Definition>? {
            val gson = Gson()
            val type = object : TypeToken<List<Definition>>() {}.type
            return gson.fromJson(gsonStr, type)
        }

        @JvmStatic
        @TypeConverter
        fun fromDefs(defs: MutableList<Definition>): String? {
            val gson = Gson()
            val type = object : TypeToken<List<Definition>>() {}.type
            return gson.toJson(defs, type)
        }
    }
}