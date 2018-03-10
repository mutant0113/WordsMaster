package com.mutant.wordsmaster.data.source.model

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// TODO implements @TypeConverter
class DefConverter {

    @TypeConverter
    fun toDefs(gsonStr: String): ArrayList<Definition> {
        val type = object : TypeToken<List<Definition>>() {}.type
        return Gson().fromJson(gsonStr, type)
    }

    @TypeConverter
    fun fromDefsToJson(defs: ArrayList<Definition>): String {
        val type = object : TypeToken<List<Definition>>() {}.type
        return Gson().toJson(defs, type)
    }

    @TypeConverter
    fun toExamples(gsonStr: String): ArrayList<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(gsonStr, type)
    }

    @TypeConverter
    fun fromExamplesToJson(examples: ArrayList<String>): String {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().toJson(examples, type)
    }

}