package com.mutant.wordsmaster.words

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ContentFrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.data.source.model.Definition
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.util.Tts
import com.mutant.wordsmaster.util.ui.ItemListener
import com.mutant.wordsmaster.util.ui.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_words.*
import kotlinx.android.synthetic.main.fragment_words.*
import kotlinx.android.synthetic.main.fragment_words.view.*
import kotlinx.android.synthetic.main.item_def.view.*
import kotlinx.android.synthetic.main.item_words.view.*
import java.util.*

/**
 * A placeholder fragment containing a simple view.
 */
class WordsFragment : Fragment(), WordsContract.View {

    private var mPresenter: WordsContract.Presenter? = null
    private lateinit var mListAdapter: WordsAdapter
    private var mTts: Tts? = null

    companion object {

        fun newInstance(): WordsFragment {
            return WordsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTts = Tts.newInstance(requireContext().applicationContext)
        mListAdapter = WordsAdapter(requireActivity(), LinkedList(), mItemListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_words, container, false)
        // Set up floating action button
        val fab = requireActivity().fab_add_word as FloatingActionButton
        fab.setOnClickListener { mPresenter?.addNewWord() }

        // use a linear layout manager
        val recyclerViewWords = root.recycler_view_words
        recyclerViewWords.layoutManager = LinearLayoutManager(activity)
        recyclerViewWords.adapter = mListAdapter
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mItemListener))
        itemTouchHelper.attachToRecyclerView(recyclerViewWords)

        recyclerViewWords.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) fab.hide()
                else fab.show()
                super.onScrolled(recyclerView, dx, dy)
            }
        })
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
    override fun showWords(words: MutableList<Word>) {
        mListAdapter.replaceData(words)

        view?.recycler_view_words?.visibility = View.VISIBLE
        view?.linearLayout_no_words?.visibility = View.GONE
    }

    // TODO logical should move to presenter
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
            val words = mListAdapter.getData()
            val word1 = words[fromPosition]
            val word2 = words[toPosition]
            mPresenter?.swap(word1 = word1, word2 = word2)
            Collections.swap(words, fromPosition, toPosition)

            // Need to swap id otherwise, update db will go wrong.
            word1.id = word2.id.also { word2.id = word1.id }
            mListAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onItemSwipe(position: Int) {
            mPresenter?.deleteWord(mListAdapter.getData()[position].id)
            mListAdapter.getData().removeAt(position)
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

    override fun onDestroy() {
        mTts?.release()
        super.onDestroy()
    }

    inner class WordsAdapter(private val mActivity: Activity,
                             private var mWords: MutableList<Word>,
                             private val mItemListener: ItemListener<Word>) :
            RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

        private var mExpandedPosition = -1
        private var mPreExpandedPosition = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_words, parent, false)
            val holder = ViewHolder(itemView, itemView.constraint_layout_top, itemView.text_view_title,
                    itemView.image_view_pron, itemView.frame_layout_click_to_expand, itemView.image_view_expand,
                    itemView.linear_layout_card_def)
            holder.mConstrainLayoutTop.setOnClickListener {
                val position = holder.adapterPosition
                val intent = AddEditWordActivity.getIntent(requireContext(), mWords[position].title)
                requireContext().startActivity(intent)
            }

            holder.mFrameLayoutClickToExpand.setOnClickListener {
                val position = holder.adapterPosition
                val isExpanded = position == mExpandedPosition

                mExpandedPosition = if (isExpanded) -1 else position
                notifyItemChanged(mPreExpandedPosition)
                notifyItemChanged(position)
            }

            holder.mImageViewPron.setOnClickListener {
                mTts?.speak(holder.mTextViewTitle.text)
            }

            return holder
        }

        fun replaceData(words: MutableList<Word>) {
            setList(words)
            notifyDataSetChanged()
        }

        private fun setList(words: MutableList<Word>) {
            mWords = words
        }

        override fun getItemCount(): Int {
            return mWords.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val adapterPosition = holder.adapterPosition
            val isExpanded = adapterPosition == mExpandedPosition
            holder.mLinearLayoutDef.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.itemView.isActivated = isExpanded

            if (isExpanded) {
                mPreExpandedPosition = adapterPosition
                holder.mImagerViewExpand.setImageResource(R.drawable.ic_expand_less_white_24px)
            } else {
                holder.mImagerViewExpand.setImageResource(R.drawable.ic_expand_more_white_24px)
            }

            val word = mWords[adapterPosition]
            setTitle(holder, word.title)
            setDefinition(holder, word.definitions)
        }

        private fun setTitle(holder: ViewHolder, title: String) {
            holder.mTextViewTitle.text = title
        }

        // TODO maybe reduce code
        private fun setDefinition(holder: ViewHolder, definitions: List<Definition>) {
            holder.mLinearLayoutDef.removeAllViews()
            for (def in definitions)
                holder.mLinearLayoutDef.addView(getDefView(definition = def))
        }

        private fun getDefView(definition: Definition): View {
            val root = View.inflate(mActivity, R.layout.item_def, null)
            root.text_view_pos.text = definition.pos
            root.text_view_def.text = definition.def
            return root
        }

        fun getData(): MutableList<Word> {
            return mWords
        }

        inner class ViewHolder(mItemView: View,
                               val mConstrainLayoutTop: ConstraintLayout,
                               val mTextViewTitle: TextView,
                               val mImageViewPron: ImageView,
                               val mFrameLayoutClickToExpand: ContentFrameLayout,
                               val mImagerViewExpand: ImageView,
                               val mLinearLayoutDef: LinearLayoutCompat) :
                RecyclerView.ViewHolder(mItemView)
    }
}