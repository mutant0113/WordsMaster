package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.addeditword.contract.AddEditWordContract
import com.mutant.wordsmaster.data.source.model.Definition
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.util.Tts
import com.mutant.wordsmaster.util.trace.DebugHelper
import com.mutant.wordsmaster.util.ui.ItemListener
import com.mutant.wordsmaster.util.ui.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_addword.*
import kotlinx.android.synthetic.main.fragment_addword.view.*
import kotlinx.android.synthetic.main.item_def.view.*
import kotlinx.android.synthetic.main.item_examples.view.*
import java.util.*


class AddEditWordFragment : Fragment(), AddEditWordContract.View {

    private var mPresenter: AddEditWordContract.Present? = null
    private var mDefinitions = mutableListOf<Definition>()
    private lateinit var mExampleAdapter: ExamplesAdapter
    private var mTts: Tts? = null
    private lateinit var mWordId: String

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAd()
        mExampleAdapter = ExamplesAdapter(arrayListOf(), mItemListener = mItemListener)
        mTts = Tts.newInstance(context.applicationContext)
    }

    private fun initAd() {
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = getString(R.string.ADMOB_SEARCH_INTERSTITIAL)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = mAdListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_addword, container, false)
        (activity as AddEditWordActivity).setSupportActionBar(toolbar)

        root.fab_edit_done.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                DebugHelper.d(TAG, "The interstitial wasn't loaded yet.")
            }
        }

        val recyclerViewExample = root.recycler_view_example
        recyclerViewExample.layoutManager = LinearLayoutManager(activity)
        recyclerViewExample.adapter = mExampleAdapter

        root.image_view_pron_add_edit.setOnClickListener({ mTts?.speak(toolbar.title) })
        root.collapsing_toolbar_layout.setOnClickListener({ mTts?.speak(toolbar.title) })

        root.fab_edit.setOnClickListener({ setEditMode(true) })

//        root.scroll_view.setOnScrollChangeListener(
//                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//                    if (scrollY > oldScrollY) {
//                        root.fab_edit_done.hide()
//                    } else {
//                        root.fab_edit_done.show()
//                    }
//                })
        return root
    }

    private val mAdListener = object : AdListener() {
        override fun onAdLoaded() {
            // Code to be executed when an ad finishes loading.
        }

        override fun onAdFailedToLoad(errorCode: Int) {
            // Code to be executed when an ad request fails.
        }

        override fun onAdOpened() {
            // Code to be executed when the ad is displayed.
        }

        override fun onAdLeftApplication() {
            // Code to be executed when the user has left the app.
        }

        override fun onAdClosed() {
            val word = Word(mWordId, collapsing_toolbar_layout.title.toString(),
                    mDefinitions, mExampleAdapter.getData())
            mPresenter?.saveWord(word)
        }
    }

    override fun onResume() {
        super.onResume()
        mPresenter?.start()
    }

    override fun onDestroy() {
        mTts?.release()
        super.onDestroy()
    }

    companion object {
        private val TAG = AddEditWordFragment::class.java.simpleName
        const val ARGUMENT_WORD_TITLE = "EDIT_WORD_TITLE"

        fun newInstance(): AddEditWordFragment {
            return AddEditWordFragment()
        }

        fun newInstance(wordTitle: String): AddEditWordFragment {
            val bundle = Bundle()
            bundle.putString(ARGUMENT_WORD_TITLE, wordTitle)
            val fragment = AddEditWordFragment()
            fragment.arguments = bundle
            return fragment
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

    override fun setView(word: Word) {
        mWordId = word.id
        setTitle(word.title)
        setDefinition(word.definitions)
        setExample(word.examples)
    }

    private fun setTitle(title: String) {
        toolbar.title = title
    }

    private fun setDefinition(definitions: MutableList<Definition>) {
        mDefinitions = definitions
        for (def in definitions)
            linear_layout_def.addView(getDefView(definition = def))
    }

    override fun setEditMode(isEditMode: Boolean) {
        setItemTouchHelper(isEditMode)
        mExampleAdapter.setEditMode(isEditMode)
        if (isEditMode) {
            fab_edit.hide()
            fab_edit_done.show()
        } else {
            fab_edit.show()
            fab_edit_done.hide()
        }
    }

    private fun setItemTouchHelper(isEditMode: Boolean) {
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mItemListener))
        if (isEditMode) {
            itemTouchHelper.attachToRecyclerView(recycler_view_example)
        } else {
            itemTouchHelper.attachToRecyclerView(null)
        }
    }

    private fun getDefView(definition: Definition): View {
        val root = this.layoutInflater.inflate(R.layout.item_def, null, false)
        root.text_view_pos.text = definition.pos
        root.text_view_def.text = definition.def
        return root
    }

    private val mItemListener = object : ItemListener<String> {

        override fun onItemClick(data: String) {
            // do nothing
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            Collections.swap(mExampleAdapter.getData(), fromPosition, toPosition)
            mExampleAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onItemSwipe(position: Int) {
            mExampleAdapter.getData().removeAt(position)
            mExampleAdapter.notifyItemRemoved(position)
        }
    }

    private fun setExample(examples: MutableList<String>) {
        mExampleAdapter.replaceData(examples)
    }

    class ExamplesAdapter(private var mExample: MutableList<String>,
                          private val mItemListener: ItemListener<String>) :
            RecyclerView.Adapter<ExamplesAdapter.ViewHolder>() {

        private var mIsEditMode = false

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_examples, parent, false)
            val holder = ViewHolder(itemView, itemView.image_view_vert, itemView.text_view_index,
                    itemView.text_view_example)
            itemView.setOnClickListener({
                mItemListener.onItemClick(mExample[holder.adapterPosition])
            })
            return holder
        }

        fun replaceData(examples: MutableList<String>) {
            setData(examples)
            notifyDataSetChanged()
        }

        private fun setData(examples: MutableList<String>) {
            mExample = examples
        }

        override fun getItemCount(): Int {
            return mExample.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            val example = mExample[position]
            holder?.mTextViewExample?.text = example
            holder?.mImageViewVert?.visibility = if (mIsEditMode) View.VISIBLE else View.GONE
            holder?.mTextViewIndex?.visibility = if (mIsEditMode) View.GONE else View.VISIBLE
            holder?.mTextViewIndex?.text = position.toString()
        }

        fun getData(): MutableList<String> {
            return mExample
        }

        fun setEditMode(isEditMode: Boolean) {
            mIsEditMode = isEditMode
            notifyDataSetChanged()
        }

        class ViewHolder(mItemView: View, val mImageViewVert: ImageView, val mTextViewIndex: TextView,
                         val mTextViewExample: TextView) :
                RecyclerView.ViewHolder(mItemView)

    }

    override fun isActive(): Boolean {
        return isAdded
    }

}