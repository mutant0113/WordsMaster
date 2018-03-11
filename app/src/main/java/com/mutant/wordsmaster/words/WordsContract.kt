package com.mutant.wordsmaster.words

import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.util.BasePresenter
import com.mutant.wordsmaster.util.BaseView

interface WordsContract {

    interface View : BaseView<Presenter> {

        fun setLoadingIndicator(active: Boolean)

        fun showWords(words: MutableList<Word>)

        fun showAddWord()

        fun showLoadingWordsError()

        fun showNoWords()

        fun showSuccessfullySavedMessage()

        fun isActive(): Boolean
    }

    interface Presenter : BasePresenter {

        fun result(requestCode: Int, resultCode: Int)

        fun loadWords(forceUpdate: Boolean)

        fun addNewWord()

        fun deleteWord(wordId: String)

        fun swap(word1: Word, word2: Word)
    }

    // TODO
    // interface Model : BaseModel {}
}