package com.mutant.wordsmaster.data.source

import android.content.Context
import com.mutant.wordsmaster.data.Word

interface WordsRemoteContract {

    interface GetWordCallback {

        fun onWordLoaded(word: Word?)

        fun onDataNotAvailable()
    }

    fun getWord(context: Context, sourceText: String, callback: GetWordCallback)
}