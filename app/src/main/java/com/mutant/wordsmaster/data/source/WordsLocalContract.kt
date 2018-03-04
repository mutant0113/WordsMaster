package com.mutant.wordsmaster.data.source

import com.mutant.wordsmaster.data.Word

interface WordsLocalContract {

    interface LoadWordsCallback {
        fun onWordsLoaded(words: List<Word>)
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
    fun swapPosition(wordId1: String, wordId2: String)
    fun refreshWords()
}