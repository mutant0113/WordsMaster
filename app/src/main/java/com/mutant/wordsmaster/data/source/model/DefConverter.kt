package com.mutant.wordsmaster.data.source.model

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// TODO implements @TypeConverter
class DefConverter {

    @TypeConverter
    fun toDefs(gsonStr: String): MutableList<Definition> {
        val type = object : TypeToken<List<Definition>>() {}.type
        return Gson().fromJson(gsonStr, type)
    }

    @TypeConverter
    fun fromDefsToJson(defs: MutableList<Definition>): String {
        val type = object : TypeToken<List<Definition>>() {}.type
        return Gson().toJson(defs, type)
    }

    @TypeConverter
    fun toExamples(gsonStr: String): MutableList<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(gsonStr, type)
    }

    @TypeConverter
    fun fromExamplesToJson(examples: MutableList<String>): String {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().toJson(examples, type)
    }

}