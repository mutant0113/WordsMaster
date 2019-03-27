package com.mutant.wordsmaster.words

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mutant.wordsmaster.data.source.model.Definition
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.databinding.ItemDefBinding
import com.mutant.wordsmaster.databinding.ItemWordsBinding

class WordsAdapter(private val viewModel: WordsViewModel?) :
        RecyclerView.Adapter<WordsAdapter.ViewHolder>() {

    private var expandedPosition = -1
    private var words = mutableListOf<Word>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWordsBinding.inflate(LayoutInflater.from(parent.context), parent,
                false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(words[position], position, position == expandedPosition)
    }

    override fun getItemCount(): Int {
        return words.size
    }

    fun replaceData(words: MutableList<Word>) {
        this.words = words
        notifyDataSetChanged()
    }

    fun getData(): MutableList<Word> {
        return words
    }

    inner class ViewHolder(private val binding: ItemWordsBinding) :
            RecyclerView.ViewHolder(binding.root), WordsListener {

        init {
            binding.listener = this@ViewHolder
        }

        fun bind(word: Word, position: Int, isExpanded: Boolean) {
            with(binding) {
                this.word = word
                this.position = position
                this.isExpanded = isExpanded
            }
            setDefinition(word.definitions)
        }

        // TODO maybe reduce code
        private fun setDefinition(definitions: List<Definition>) {
            with(binding.linearLayoutDef) {
                removeAllViews()
                for (def in definitions) {
                    addView(getDefView(def))
                }
            }
        }

        private fun getDefView(definition: Definition): View {
            val binding = ItemDefBinding.inflate(LayoutInflater.from(binding.root.context)).apply {
                this.definition = definition
            }
            return binding.root
        }

        override fun onNavigatorAddEditWordActivity(title: String) {
            viewModel?.openAddEditWordActivity(title)
        }

        override fun onExpandDef(word: Word) {
            val position = words.indexOf(word)
            val isExpanded = position == expandedPosition
            val preExpandedPosition = expandedPosition
            expandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(preExpandedPosition)
            notifyItemChanged(expandedPosition)
        }

        override fun onTtsSpeak(title: String) {
            viewModel?.setTtsWord(title)
        }
    }
}