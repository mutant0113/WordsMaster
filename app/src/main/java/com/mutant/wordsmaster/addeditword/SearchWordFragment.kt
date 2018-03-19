package com.mutant.wordsmaster.addeditword

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.contract.AddEditWordContract
import com.mutant.wordsmaster.addeditword.contract.SearchWordContract
import com.mutant.wordsmaster.util.Utils
import kotlinx.android.synthetic.main.fragment_search_word.*
import kotlinx.android.synthetic.main.fragment_search_word.view.*


class SearchWordFragment : Fragment(), SearchWordContract.View {

    private var mPresenter: AddEditWordContract.Present? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_search_word, container, false)
        root.image_view_search.setOnClickListener(mSearchListener)
        root.button_custom.setOnClickListener(mCustomListener)
        return root
    }

    private val mSearchListener = View.OnClickListener {
        val sourceText = edit_text_word.text.toString()
        mPresenter?.getWordByTitle(sourceText)
    }

    private val mCustomListener = View.OnClickListener { showWord(true) }

    override fun setPresent(present: AddEditWordPresenter) {
        mPresenter = present
    }

    override fun showNoSuchWordError() {
        setLoadingIndicator(false)
        setSearchButtonEnabled(true)
        text_view_no_such_word_warning.visibility = View.VISIBLE
        button_custom.visibility = View.VISIBLE
    }

    override fun showKeyboard() {
        Utils.showKeyBoard(activity, edit_text_word)
    }

    override fun showWord(isEditMode: Boolean) {
        setLoadingIndicator(false)
        setSearchButtonEnabled(true)
        Utils.hideKeyBoard(activity, edit_text_word)
        (activity as AddEditWordActivity).showWord(isEditMode)
    }

    override fun isActive(): Boolean {
        return view != null
    }

    override fun onResume() {
        super.onResume()
//        mPresenter?.start()
    }

    override fun setLoadingIndicator(active: Boolean) {
        progress_bar.visibility = if (active) View.VISIBLE else View.INVISIBLE
    }

    override fun setSearchButtonEnabled(enabled: Boolean) {
        image_view_search.isEnabled = enabled
    }

    override fun showSearching() {
        setLoadingIndicator(true)
        setSearchButtonEnabled(false)
        text_view_no_such_word_warning.visibility = View.GONE
    }

    override fun setHistory(wordsHistory: MutableList<String>) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, wordsHistory)
        edit_text_word.setAdapter(adapter)
    }

    companion object {

        fun newInstance(): SearchWordFragment {
            return SearchWordFragment()
        }
    }

}
