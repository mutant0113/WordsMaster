package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.contract.AddEditWordContract
import com.mutant.wordsmaster.data.source.model.Definition
import com.mutant.wordsmaster.data.source.model.Word
import kotlinx.android.synthetic.main.fragment_addword.*
import kotlinx.android.synthetic.main.fragment_addword.view.*
import kotlinx.android.synthetic.main.item_def.view.*
import kotlinx.android.synthetic.main.item_examples.view.*


class AddEditWordFragment : Fragment(), AddEditWordContract.View {

    private var mPresenter: AddEditWordContract.Present? = null
    private var mDefinitions = arrayListOf<Definition>()
    private lateinit var mExampleAdapter: ExamplesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_addword, container, false)
        (activity as AddEditWordActivity).setSupportActionBar(toolbar)

        root.fab_edit_word_done.setOnClickListener {
            mPresenter?.saveWord(collapsing_toolbar_layout.title.toString(),
                    mDefinitions, mExampleAdapter.getData())
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
        mDefinitions = definitions
        for (def in definitions)
            linear_layout_def.addView(getDefView(definition = def))
    }

    private fun getDefView(definition: Definition): View {
        val root = this.layoutInflater.inflate(R.layout.item_def, null, false)
        root.text_view_pos.text = definition.pos
        root.text_view_def.text = definition.def
        return root
    }

    override fun setExample(examples: ArrayList<String>) {
        mExampleAdapter = ExamplesAdapter(examples, object : WordsItemListener {

            override fun onItemClick(clickedExample: String) {
            }

            override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
                return false
            }

            override fun onItemSwipe(position: Int) {
            }
        })
        recycler_view_example.layoutManager = LinearLayoutManager(activity)
        recycler_view_example.adapter = mExampleAdapter
    }

    class ExamplesAdapter(private var mExample: ArrayList<String>,
                          private val mWordsItemListener: WordsItemListener) :
            RecyclerView.Adapter<ExamplesAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_examples, parent, false)
            val holder = ViewHolder(itemView, itemView.text_view_example)
            itemView.setOnClickListener({
                mWordsItemListener.onItemClick(mExample[holder.adapterPosition])
            })
            return holder
        }

        fun replaceData(word: Word) {
            setData(word.examples)
            notifyDataSetChanged()
        }

        private fun setData(examples: ArrayList<String>) {
            mExample = examples
        }

        override fun getItemCount(): Int {
            return mExample.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val example = mExample[position]
            holder?.mTextViewExample?.text = example
        }

        fun getData(): ArrayList<String> {
            return mExample
        }

        class ViewHolder(mItemView: View, val mTextViewExample: TextView) :
                RecyclerView.ViewHolder(mItemView)

    }

    interface WordsItemListener {

        fun onItemClick(clickedExample: String)

        fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

        fun onItemSwipe(position: Int)
    }

    override fun isActive(): Boolean {
        return isAdded
    }
}