package com.mutant.wordsmaster.data.source

import com.mutant.wordsmaster.data.source.model.Word

interface WordsRemoteContract {

    interface GetWordCallback {

        fun onWordLoaded(word: Word)

        fun onDataNotAvailable()
    }

    fun parseHtml(html: String, callback: GetWordCallback)
}