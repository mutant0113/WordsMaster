package com.mutant.wordsmaster.words

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
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
import com.mutant.wordsmaster.data.Word
import kotlinx.android.synthetic.main.activity_words.*
import kotlinx.android.synthetic.main.fragment_words.*
import kotlinx.android.synthetic.main.fragment_words.view.*
import kotlinx.android.synthetic.main.view_words.view.*
import java.util.*

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
        mListAdapter?.replaceData(words)

        view?.recycler_view_words?.visibility = View.VISIBLE
        view?.linearLayout_no_words?.visibility = View.GONE
    }

    /**
     * Listener for clicks and swipes on words in the ListView.
     */
    private val mItemListener = object : WordsItemListener {

        override fun onItemClick(clickedWord: Word) {
            val intent = Intent(context, AddEditWordActivity::class.java)
            // TODO
//            startActivity(intent)
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            Collections.swap(mListAdapter?.getData(), fromPosition, toPosition)
            mListAdapter?.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onItemSwipe(position: Int) {
            mPresenter?.deleteWord(mListAdapter?.getData()!![position].id)
            mListAdapter?.notifyItemRemoved(position)
        }
    }

    private class ItemTouchHelperCallback(private val mItemListener: WordsItemListener) :
            ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            return mItemListener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            mItemListener.onItemSwipe(viewHolder.adapterPosition)
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.7f
                viewHolder?.itemView?.setBackgroundColor(Color.YELLOW)
            }
        }

        override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
            super.clearView(recyclerView, viewHolder)
            clearHighlight(viewHolder)
        }

        override fun onChildDraw(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                val width: Int? = viewHolder?.itemView?.width
                val alpha = if (width != null) 1 - Math.abs(dX) / width else 1f

                viewHolder?.itemView?.alpha = alpha
                // Set background color to white when dx is 0 which means not to swipe
                if (dX == 0f) viewHolder?.itemView?.setBackgroundColor(Color.WHITE)
                else viewHolder?.itemView?.setBackgroundColor(Color.YELLOW)
            } else if (actionState == ItemTouchHelper.ANIMATION_TYPE_SWIPE_CANCEL) {
                clearHighlight(viewHolder)
            }
        }

        fun clearHighlight(viewHolder: RecyclerView.ViewHolder?) {
            viewHolder?.itemView?.alpha = 1.0f
            viewHolder?.itemView?.setBackgroundColor(Color.WHITE)
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
                       private val mWordsItemListener: WordsItemListener) : RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.view_words, parent, false)
            val holder = ViewHolder(itemView, itemView.text_view_title, itemView.text_view_explanation, itemView.text_view_eg)
            itemView.setOnClickListener({
                mWordsItemListener.onItemClick(mWords[holder.adapterPosition])
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

        fun getData(): List<Word> {
            return mWords
        }

        class ViewHolder(mItemView: View,
                         val mTextViewTitle: TextView,
                         val mTextViewExplanation: TextView,
                         val mTextViewEg: TextView) : RecyclerView.ViewHolder(mItemView)

    }

    interface WordsItemListener {

        fun onItemClick(clickedWord: Word)

        fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

        fun onItemSwipe(position: Int)
    }

}
