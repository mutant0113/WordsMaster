package com.mutant.wordsmaster.words

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.data.Word
import kotlinx.android.synthetic.main.activity_words.*
import kotlinx.android.synthetic.main.fragment_words.*
import kotlinx.android.synthetic.main.fragment_words.view.*
import kotlinx.android.synthetic.main.view_words.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class WordsFragment : Fragment(), WordsContract.View {

    private var mPresenter: WordsContract.Presenter? = null
    private var mListAdapter: WordsAdapter? = null

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

        fab.setImageResource(R.drawable.ic_add)
        fab.setOnClickListener { mPresenter?.addNewWord() }

        // use a linear layout manager
        root.recycler_view_words.layoutManager = LinearLayoutManager(activity)
        root.recycler_view_words.adapter = mListAdapter
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
        mListAdapter?.replaceData(words)

        view?.recycler_view_words?.visibility = View.VISIBLE
        view?.linearLayout_no_words?.visibility = View.GONE
    }

    /**
     * Listener for clicks on words in the ListView.
     */
    private val mItemListener = object : WordsItemListener {

        override fun onWordClick(clickedWord: Word) {
            TODO("intent to detail activity")
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showNoWords() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_word_message))
    }

    private fun showMessage(message: String) {
        if(view != null)
            Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }

    override fun isActive(): Boolean {
        return isAdded
    }

    class WordsAdapter(private var mWords: List<Word>,
                       private val mWordsItemListener: WordsItemListener) : RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.view_words, parent, false)
            val holder = ViewHolder(itemView, itemView.text_view_title, itemView.text_view_explanation, itemView.text_view_eg)
            itemView.setOnClickListener({
                mWordsItemListener.onWordClick(mWords[holder.adapterPosition])
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
            holder?.mTextViewExplanation?.text = word.explanation
            holder?.mTextViewEg?.text = word.eg
        }

        class ViewHolder(mItemView: View,
                         val mTextViewTitle: TextView,
                         val mTextViewExplanation: TextView,
                         val mTextViewEg: TextView) : RecyclerView.ViewHolder(mItemView)
    }

    interface WordsItemListener {

        fun onWordClick(clickedWord: Word)
    }

}
