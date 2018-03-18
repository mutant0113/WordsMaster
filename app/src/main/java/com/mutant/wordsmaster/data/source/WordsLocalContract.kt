package com.mutant.wordsmaster.data.source

import com.mutant.wordsmaster.data.source.model.Word

interface WordsLocalContract {

    interface LoadWordsCallback {
        fun onWordsLoaded(words: MutableList<Word>)
        fun onDataNotAvailable()
    }

    interface GetWordCallback {

        fun onWordLoaded(word: Word)

        fun onDataNotAvailable()
    }

    fun getWords(callback: LoadWordsCallback)
    fun getWord(wordId: String, callback: GetWordCallback)
    fun saveWord(word: Word)
    fun deleteWord(wordId: String)
    fun deleteAllWords()
    fun swap(word1: Word, word2: Word)
    fun refreshWords()
}