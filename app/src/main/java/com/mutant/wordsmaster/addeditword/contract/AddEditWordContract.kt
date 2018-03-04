package com.mutant.wordsmaster.addeditword.contract

import android.app.Activity
import android.content.Context
import com.mutant.wordsmaster.addeditword.AddEditWordPresenter
import com.mutant.wordsmaster.util.BasePresenter
import com.mutant.wordsmaster.util.BaseView

interface AddEditWordContract {

    interface View : BaseView<AddEditWordPresenter> {

        fun showEmptyWordError()

        fun showWordsList()

        fun setTitle(title: String)

        fun setExplanation(explanation: String?)

        fun setEg(eg: String?)

        fun isActive(): Boolean

    }

    interface Present : BasePresenter {

        fun saveWord(title: String, explanation: String?, eg: String?)

        /**
         * If it is in edition, populate TextViews with data
         */
        fun populateWord()

        fun isDataMissing(): Boolean

        fun parseHtmlFromWebView(context: Context, sourceText: String)

        fun translate(activity: Activity, sourceText: String)

    }
}