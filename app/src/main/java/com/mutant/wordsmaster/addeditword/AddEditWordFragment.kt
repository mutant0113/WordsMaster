package com.mutant.wordsmaster.addeditword

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.mutant.wordsmaster.R
import com.mutant.wordsmaster.data.source.model.Definition
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.databinding.FragmentAddwordBinding
import com.mutant.wordsmaster.util.Tts
import com.mutant.wordsmaster.util.trace.DebugHelper
import com.mutant.wordsmaster.util.ui.ItemListener
import com.mutant.wordsmaster.util.ui.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_addword.*
import kotlinx.android.synthetic.main.item_def.view.*
import java.util.*

class AddEditWordFragment : Fragment() {

    private lateinit var binding: FragmentAddwordBinding

    private lateinit var wordId: String
    private lateinit var exampleAdapter: ExamplesAdapter
    private var definitions = mutableListOf<Definition>()
    private var tts: Tts? = null

    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAd()
        exampleAdapter = ExamplesAdapter(arrayListOf(), mItemListener = mItemListener)
        tts = Tts.newInstance(requireContext().applicationContext)
    }

    private fun initAd() {
        interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = getString(R.string.ADMOB_SEARCH_INTERSTITIAL)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = adListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as AddEditWordActivity).setSupportActionBar(toolbar)
        binding = FragmentAddwordBinding.inflate(inflater, container, false).apply {
            listener = object : AddEditWordUserActionsListener {

                override fun onTtsSpeak() {
                    tts?.speak(toolbar.title)
                }

                override fun onEdit() {
                    setEditMode(true)
                }

                override fun onDone() {
                    if (interstitialAd.isLoaded) {
                        interstitialAd.show()
                    } else {
                        DebugHelper.d(TAG, "The interstitial wasn't loaded yet.")
                    }
                }
            }
//        root.scroll_view.setOnScrollChangeListener(
//                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
//                    if (scrollY > oldScrollY) {
//                        root.fab_edit_done.hide()
//                    } else {
//                        root.fab_edit_done.show()
//                    }
//                })
        }
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        with(binding) {
            viewModel = (activity as AddEditWordActivity).obtainViewModel().apply {
                word.observe(viewLifecycleOwner, Observer {
                    wordId = it.id
                    setTitle(it.title)
                    setDefinition(it.definitions)
                    setExample(it.examples)
                })

                snackBarStrId.observe(viewLifecycleOwner, Observer {
                    showMessage(it)
                })

                showWordsListEvent.observe(viewLifecycleOwner, Observer {
                    showWordsList()
                })
            }

            lifecycleOwner = viewLifecycleOwner
        }
    }

    // TODO Duplicate with [WordsFragment]. Try to merge as one method.
    private fun showMessage(messageId: Int) {
        if (view != null) {
            Snackbar.make(view!!, getString(messageId), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun showWordsList() {
        requireActivity().setResult(Activity.RESULT_OK)
        requireActivity().finish()
    }

    private fun setupRecyclerView() {
        with(binding.recyclerViewExample) {
            layoutManager = LinearLayoutManager(activity)
            adapter = exampleAdapter
        }
    }

    private val adListener = object : AdListener() {

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
            val word = Word(wordId, collapsing_toolbar_layout.title.toString(),
                    definitions, exampleAdapter.getData())
            binding.viewModel?.saveWord(word)
        }
    }

    override fun onDestroy() {
        tts?.release()
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

    private fun setTitle(title: String) {
        toolbar.title = title
    }

    private fun setDefinition(definitions: MutableList<Definition>) {
        this.definitions = definitions
        for (def in definitions)
            linear_layout_def.addView(getDefView(definition = def))
    }

    fun setEditMode(isEditMode: Boolean) {
        setItemTouchHelper(isEditMode)
        exampleAdapter.setEditMode(isEditMode)
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
        val root = View.inflate(requireContext(), R.layout.item_def, null)
        root.text_view_pos.text = definition.pos
        root.text_view_def.text = definition.def
        return root
    }

    private val mItemListener = object : ItemListener<String> {

        override fun onItemClick(data: String) {
            // do nothing
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            Collections.swap(exampleAdapter.getData(), fromPosition, toPosition)
            exampleAdapter.notifyDataSetChanged()
            return true
        }

        override fun onItemSwipe(position: Int) {
            exampleAdapter.getData().removeAt(position)
            exampleAdapter.notifyItemRemoved(position)
        }
    }

    private fun setExample(examples: MutableList<String>) {
        exampleAdapter.replaceData(examples)
    }
}