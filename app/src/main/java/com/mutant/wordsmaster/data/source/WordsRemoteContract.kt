package com.mutant.wordsmaster.data.source

import android.content.Context
import com.mutant.wordsmaster.data.source.model.Word

interface WordsRemoteContract {

    interface GetWordCallback {

        fun onWordLoaded(word: Word)

        fun onDataNotAvailable()
    }

    fun getWordByTitle(context: Context, wordTitle: String?, callback: WordsLocalContract.GetWordCallback)

}