package com.mutant.wordsmaster.words

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.databinding.FragmentWordsBinding
import com.mutant.wordsmaster.util.Tts
import com.mutant.wordsmaster.util.obtainViewModel
import com.mutant.wordsmaster.util.ui.ItemListener
import com.mutant.wordsmaster.util.ui.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_words.*
import java.util.*

class WordsFragment : Fragment() {

    private lateinit var listAdapter: WordsAdapter
    private lateinit var binding: com.mutant.wordsmaster.databinding.FragmentWordsBinding
    private lateinit var tts: Tts

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentWordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        setupTts()
        setupFab()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        binding.viewModel = obtainViewModel(WordsViewModel::class.java).apply {

            openAddEditWordEvent.observe(viewLifecycleOwner, Observer {
                val intent = AddEditWordActivity.getIntent(requireContext(), it)
                startActivity(intent)
            })

            snackBarStrId.observe(viewLifecycleOwner, Observer {
                showMessage(it)
            })

            ttsWord.observe(viewLifecycleOwner, Observer {
                tts.speak(it)
            })
        }
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupTts() {
        tts = Tts.newInstance(requireContext().applicationContext)
    }

    private fun setupFab() {
        activity?.fab_add_word?.apply {
            setOnClickListener { binding.viewModel?.openAddEditWordActivity() }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewWords.apply {
            layoutManager = LinearLayoutManager(activity)
            listAdapter = WordsAdapter(binding.viewModel)
            adapter = listAdapter

            val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mItemListener))
            itemTouchHelper.attachToRecyclerView(this)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    activity?.fab_add_word?.apply {
                        if (dy > 0) this.hide()
                        else this.show()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        binding.viewModel?.handleActivityResult(requestCode, resultCode)
    }

    override fun onResume() {
        super.onResume()
        binding.viewModel?.start()
    }

    // TODO logical should move to presenter
    /**
     * Listener for clicks and swipes on words in the ListView.
     */
    private val mItemListener = object : ItemListener<Word> {

        override fun onItemClick(data: Word) {
            val intent = Intent(context, AddEditWordActivity::class.java)
            // TODO Open edit word activity
//            startActivity(intent)
        }

        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            val words = listAdapter.getData()
            val word1 = words[fromPosition]
            val word2 = words[toPosition]
            binding.viewModel?.swap(word1 = word1, word2 = word2)
            Collections.swap(words, fromPosition, toPosition).also {
                // Need to swap id, otherwise update db will go wrong.
                word1.id = word2.id.also { word2.id = word1.id }
            }
            listAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onItemSwipe(position: Int) {
            binding.viewModel?.deleteWord(listAdapter.getData()[position].id)
            listAdapter.getData().removeAt(position)
            listAdapter.notifyItemRemoved(position)
        }
    }

    private fun showMessage(messageId: Int) {
        if (view != null) {
            Snackbar.make(view!!, getString(messageId), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        tts.release()
        super.onDestroy()
    }

    companion object {

        fun newInstance(): WordsFragment {
            return WordsFragment()
        }
    }
}