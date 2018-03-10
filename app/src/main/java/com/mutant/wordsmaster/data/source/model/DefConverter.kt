package com.mutant.wordsmaster.data.source.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// TODO implements @TypeConverter
class DefConverter {

    companion object {

        fun toDefs(gsonStr: String?): List<Definition>? {
            if(gsonStr.isNullOrBlank()) return arrayListOf()
            val type = object : TypeToken<List<Definition>>() {}.type
            return Gson().fromJson(gsonStr, type)
        }

        fun toJson(defs: List<Definition>?): String? {
            if(defs == null || defs.isEmpty()) return ""
            val type = object : TypeToken<List<Definition>>() {}.type
            return Gson().toJson(defs, type)
        }
    }

//    @TypeConverter
//    fun toDefs(gsonStr: String): List<Definition>? {
//        val gson = Gson()
//        val type = object : TypeToken<List<Definition>>() {}.type
//        return gson.fromJson(gsonStr, type)
//    }
//
//    @TypeConverter
//    fun toJson(defs: MutableList<Definition>): String? {
//        val gson = Gson()
//        val type = object : TypeToken<List<Definition>>() {}.type
//        return gson.toJson(defs, type)
//    }
}