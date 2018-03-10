package com.mutant.wordsmaster.words

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.util.ui.ItemListener
import com.mutant.wordsmaster.util.ui.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_words.*
import kotlinx.android.synthetic.main.fragment_words.*
import kotlinx.android.synthetic.main.fragment_words.view.*
import kotlinx.android.synthetic.main.item_words.view.*
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class WordsFragment : Fragment(), WordsContract.View {

    private var mPresenter: WordsContract.Presenter? = null
    private lateinit var mListAdapter: WordsAdapter

    companion object {

        fun newInstance(): WordsFragment {
            return WordsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mListAdapter = WordsAdapter(listOf(), mItemListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_words, container, false)
        // Set up floating action button
        val fab = activity.fab_add_word as FloatingActionButton
        fab.setOnClickListener { mPresenter?.addNewWord() }

        // use a linear layout manager
        val recyclerViewWords = root.recycler_view_words
        recyclerViewWords.layoutManager = LinearLayoutManager(activity)
        recyclerViewWords.adapter = mListAdapter
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mItemListener))
        itemTouchHelper.attachToRecyclerView(recyclerViewWords)
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mPresenter?.result(requestCode, resultCode)
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.start()
    }

    override fun setPresent(presenter: WordsContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (view == null) return
        if (active) progressBar_loading.visibility = View.VISIBLE
        else progressBar_loading.visibility = View.INVISIBLE
    }

    /**
     * Show all words in the CardView.
     */
    override fun showWords(words: List<Word>) {
        mListAdapter.replaceData(words)

        view?.recycler_view_words?.visibility = View.VISIBLE
        view?.linearLayout_no_words?.visibility = View.GONE
    }

    /**
     * Listener for clicks and swipes on words in the ListView.
     */
    private val mItemListener = object : ItemListener<Word> {

        override fun onItemClick(data: Word) {
            val intent = Intent(context, AddEditWordActivity::class.java)
            // TODO
//            startActivity(intent)
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            Collections.swap(mListAdapter.getData(), fromPosition, toPosition)
            mListAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onItemSwipe(position: Int) {
            mPresenter?.deleteWord(mListAdapter.getData()[position].id)
            mListAdapter.notifyItemRemoved(position)
        }
    }

    /**
     * intent to {@link AddEditWordActivity}
     */
    override fun showAddWord() {
        val intent = Intent(context, AddEditWordActivity::class.java)
        startActivityForResult(intent, AddEditWordActivity.REQUEST_ADD_TASK)
    }

    override fun showLoadingWordsError() {
        showMessage(getString(R.string.loading_words_error))
    }

    override fun showNoWords() {
        view?.recycler_view_words?.visibility = View.GONE
        view?.linearLayout_no_words?.visibility = View.VISIBLE
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_word_message))
    }

    private fun showMessage(message: String) {
        if (view != null)
            Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }

    override fun isActive(): Boolean {
        return isAdded
    }

    class WordsAdapter(private var mWords: List<Word>,
                       private val mItemListener: ItemListener<Word>) :
            RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_words, parent, false)
            val params = itemView.layoutParams
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT
//            params.height = 400
            itemView.layoutParams = params
            val holder = ViewHolder(itemView, itemView.text_view_title, itemView.text_view_explanation, itemView.text_view_eg)
            itemView.setOnClickListener({
                mItemListener.onItemClick(mWords[holder.adapterPosition])
            })
            return holder
        }

        fun replaceData(words: List<Word>) {
            setList(words)
            notifyDataSetChanged()
        }

        private fun setList(words: List<Word>) {
            mWords = words
        }

        override fun getItemCount(): Int {
            return mWords.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val word = mWords[position]
            holder?.mTextViewTitle?.text = word.title
            // TODO
            if (word.definitions.isNotEmpty()) {
                holder?.mTextViewDef?.text = word.definitions[0].def
            }
            holder?.mTextViewExample?.text = word.examples[0]
        }

        fun getData(): List<Word> {
            return mWords
        }

        class ViewHolder(mItemView: View,
                         val mTextViewTitle: TextView,
                         val mTextViewDef: TextView,
                         val mTextViewExample: TextView) : RecyclerView.ViewHolder(mItemView)

    }

}
