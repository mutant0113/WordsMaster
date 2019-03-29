package com.mutant.wordsmaster.addeditword

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mutant.wordsmaster.databinding.ItemExamplesBinding
import com.mutant.wordsmaster.util.ui.ItemListener
import kotlinx.android.synthetic.main.item_examples_header.view.*

class ExamplesAdapter(private var examples: MutableList<String>,
                      private val mItemListener: ItemListener<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var editMode = false
    private val HEADER = 1
    private val LIST = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemExamplesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (viewType) {
            HEADER -> getHeaderHolder(binding)
            else -> getListHolder(binding)
        }
    }

    private fun getHeaderHolder(binding: ItemExamplesBinding) = ViewHolderHeader(binding)

    private fun getListHolder(binding: ItemExamplesBinding) = ViewHolderList(binding)

    override fun getItemViewType(position: Int): Int {
        return if (editMode) {
            when (position) {
                0 -> HEADER
                else -> LIST
            }
        } else LIST

    }

    fun replaceData(examples: MutableList<String>) {
        setData(examples)
        notifyDataSetChanged()
    }

    private fun setData(examples: MutableList<String>) {
        this.examples = examples
    }

    override fun getItemCount(): Int {
        return if (editMode) examples.size + 1 else examples.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderList) {
            // Item count will be increased by 1 if in edit mode
            val adjustPosition = if (editMode) position - 1 else position
            holder.bind(examples[adjustPosition])
        }
    }

    fun getData(): MutableList<String> {
        return examples
    }

    fun setEditMode(editMode: Boolean) {
        this.editMode = editMode
        notifyDataSetChanged()
    }

    inner class ViewHolderList(val binding: ItemExamplesBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.insertListener = View.OnClickListener {
                val example = it.edit_text_example.text.toString()
                if (!example.isBlank()) {
                    examples.add(0, example)
                    it.edit_text_example.text.clear()
                    notifyItemInserted(0)
                }
            }
        }

        fun bind(example: String) {
            binding.example = example
            binding.index = adapterPosition.toString()
            binding.editMode = editMode
        }
    }

    inner class ViewHolderHeader(binding: ItemExamplesBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // TODO should not use onItemClick to cover click listener
            binding.insertListener = View.OnClickListener {
                mItemListener.onItemClick(examples[adapterPosition])
            }
        }
    }
}