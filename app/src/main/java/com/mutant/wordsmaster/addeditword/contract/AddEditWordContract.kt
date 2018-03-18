package com.mutant.wordsmaster.addeditword.contract

import android.app.Activity
import com.mutant.wordsmaster.addeditword.AddEditWordPresenter
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.util.BasePresenter
import com.mutant.wordsmaster.util.BaseView

interface AddEditWordContract {

    interface View : BaseView<AddEditWordPresenter> {

        fun showEmptyWordError()

        fun showWordsList()

        fun setView(word: Word)

        fun setEditMode(isEditMode: Boolean)

        fun isActive(): Boolean

        fun isEditMode(): Boolean

    }

    interface Present : BasePresenter {

        fun saveWord(word: Word)

        /**
         * If it is in edition, populate TextViews with data
         */
        fun populateWord()

        fun getWordByTitle(wordTitle: String?)

        fun translate(activity: Activity, sourceText: String)

    }
}