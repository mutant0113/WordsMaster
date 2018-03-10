package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.contract.AddEditWordContract
import com.mutant.wordsmaster.data.source.model.Definition
import kotlinx.android.synthetic.main.fragment_addword.*
import kotlinx.android.synthetic.main.fragment_addword.view.*
import kotlinx.android.synthetic.main.item_def.view.*


class AddEditWordFragment : Fragment(), AddEditWordContract.View {

    private var mPresenter: AddEditWordContract.Present? = null
    private var mDefinitions = arrayListOf<Definition>()
    private var mExamples = arrayListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_addword, container, false)
        (activity as AddEditWordActivity).setSupportActionBar(toolbar)

        root.fab_edit_word_done.setOnClickListener {
            mPresenter?.saveWord(collapsing_toolbar_layout.title.toString(),
                    mDefinitions, mExamples)
        }
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
        Snackbar.make(collapsing_toolbar_layout, getString(R.string.empty_word_message), Snackbar.LENGTH_LONG).show()
    }

    override fun showWordsList() {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun setTitle(title: String) {
        toolbar.title = title
    }

    override fun setDefinition(definitions: ArrayList<Definition>) {
        if(definitions == null) return
        mDefinitions = definitions
        for(def in definitions)
            linear_layout_def.addView(getDefView(definition = def))
    }

    private fun getDefView(definition: Definition): View {
        val root = this.layoutInflater.inflate(R.layout.item_def, null, false)
        root.text_view_pos.text = definition.pos
        root.text_view_def.text = definition.def
        return root
    }

    override fun setExample(examples: ArrayList<String>) {
        // TODO adater
        edit_text_eg.setText(examples[0])
    }

    override fun isActive(): Boolean {
        return isAdded
    }
}