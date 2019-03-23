package com.mutant.wordsmaster.util

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mutant.wordsmaster.data.source.model.Word
import com.mutant.wordsmaster.words.WordsAdapter

object DataBindingMethods {

    @BindingAdapter("app:goneUnless")
    @JvmStatic
    fun goneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, words: MutableList<Word>) {
        with(recyclerView.adapter as WordsAdapter) {
            replaceData(words)
        }
    }
}