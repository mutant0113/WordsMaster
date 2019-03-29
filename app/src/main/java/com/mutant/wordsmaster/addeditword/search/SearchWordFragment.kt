package com.mutant.wordsmaster.addeditword.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mutant.wordsmaster.addeditword.AddEditWordActivity
import com.mutant.wordsmaster.databinding.FragmentSearchWordBinding
import kotlinx.android.synthetic.main.fragment_search_word.*

class SearchWordFragment : Fragment(), SearchUserActionsListener {

    private lateinit var binding: FragmentSearchWordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchWordBinding.inflate(inflater, container, false).apply {
            listener = this@SearchWordFragment
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        with(binding) {
            viewModel = (activity as AddEditWordActivity).obtainViewModel().apply {
                isNewWord.observe(viewLifecycleOwner, Observer {
                    (activity as AddEditWordActivity).showWord(it)
                })

                history.observe(viewLifecycleOwner, Observer {
                    setHistory(it)
                })
            }

            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onSearch() {
        val sourceText = edit_text_word.text.toString()
        binding.viewModel?.getWordByTitle(sourceText)
    }

    override fun onCustom() {
        (activity as AddEditWordActivity).showWord(true)
    }

    override fun onResume() {
        super.onResume()
        binding.viewModel?.start(null)
    }

    // TODO use databinding
    private fun setHistory(wordsHistory: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, wordsHistory)
        edit_text_word.setAdapter(adapter)
    }

    companion object {

        fun newInstance(): SearchWordFragment {
            return SearchWordFragment()
        }
    }
}