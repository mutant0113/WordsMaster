package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mutant.wordsmaster.R
import kotlinx.android.synthetic.main.fragment_addword.*

class AddEditWordFragment : Fragment(), AddEditWordContract.View {

    private var mPresenter: AddEditWordContract.Present? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_addword, container, false)
        val fab = activity.findViewById(R.id.fab_edit_word_done) as FloatingActionButton

        fab.setOnClickListener { mPresenter?.saveWord(edit_text_title.text.toString(),
                edit_text_explanation.text.toString(), edit_text_eg.text.toString()) }
        return root
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.start()
    }

    companion object {
        const val ARGUMENT_EDIT_WORD_ID = "EDIT_WORD_ID"

        fun newInstance(): AddEditWordFragment {
            return AddEditWordFragment()
        }
    }

    override fun setPresent(present: AddEditWordPresenter) {
        this.mPresenter = present
    }

    override fun showEmptyWordError() {
        Snackbar.make(edit_text_title, getString(R.string.empty_word_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showWordsList() {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun setTitle(title: String) {
        edit_text_title.setText(title)
    }

    override fun setExplanation(explanation: String?) {
        edit_text_explanation.setText(explanation)
    }

    override fun setEg(eg: String?) {
        edit_text_eg.setText(eg)
    }

    override fun isActive(): Boolean {
        return view != null
    }
}