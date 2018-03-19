package com.mutant.wordsmaster.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object SettingsPreferences {

    private const val PREFS_WORDS = "PREFS_WORDS"
    private const val PREF_HISTORY = "PREF_HISTORY"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_WORDS, Context.MODE_PRIVATE)
    }

    fun setHistory(context: Context, wordsHistory: MutableList<String>) {
        val sharedPreferences = getSharedPreferences(context)
        sharedPreferences.edit().putString(PREF_HISTORY, Gson().toJson(wordsHistory)).apply()
    }

    fun getHistory(context: Context): MutableList<String> {
        val sharedPreferences = getSharedPreferences(context)
        val json = sharedPreferences.getString(PREF_HISTORY, "")
        return Gson().fromJson(json, object : TypeToken<MutableList<String>>() {}.type) ?: mutableListOf()
    }
}