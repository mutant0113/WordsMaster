package com.mutant.wordsmaster.addeditword.contract

import com.mutant.wordsmaster.addeditword.AddEditWordPresenter
import com.mutant.wordsmaster.util.BaseView

interface SearchWordContract {

    interface View : BaseView<AddEditWordPresenter> {

        fun setLoadingIndicator(active: Boolean)

        fun setSearchButtonEnabled(enabled: Boolean)

        fun showNoSuchWordError()

        fun showWord(isEditMode: Boolean)

        fun showSearching()

        fun showKeyboard()

        fun isActive(): Boolean

    }

}